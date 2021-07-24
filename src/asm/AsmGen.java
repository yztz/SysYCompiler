package asm;

import asm.converter.AsmConvertOrganizer;
import genir.AbstractIR;
import genir.IRBlock;
import genir.IRFunction;
import genir.IRUnion;
import genir.code.GotoRepresent;
import genir.code.InterRepresent;
import symboltable.*;

import java.util.*;

public class AsmGen {
    SymbolTableHost symbolTableHost;

    public AsmGen(SymbolTableHost symbolTableHost) {
        this.symbolTableHost = symbolTableHost;
    }

    public String generate(IRUnion irUnion) {
        StringBuilder builder = new StringBuilder();

        List<AsmSection> staticDataSection = genStaticData();
        for (AbstractIR ir : irUnion.getAll()) {
            if (ir instanceof IRFunction) {
                AsmSection funcSection = genFunction((IRFunction) ir);
                funcSection.getText(builder);
            }
        }

        for (AsmSection dataSection : staticDataSection) {
            dataSection.getText(builder);
        }

        return builder.toString();
    }

    public AsmSection genFunction(IRFunction irFunction) {
        FuncSymbol funcSymbol = irFunction.funcSymbol;
        AsmBuilder asmBuilder = new AsmBuilder(funcSymbol.getAsmLabel());
        asmBuilder.text().align(2).global().arch("armv7-a").fpu("vfp").type(AsmBuilder.Type.Function).label();

        genFunctionGenericStart(asmBuilder, funcSymbol);

        List<IRBlock> irBlocks = divideIntoBlock(irFunction);
        calNextRef(irBlocks);

        RegGetter regGetter = new RegGetterImpl();
        // todo 汇编代码生成

        for (IRBlock irBlock : irBlocks) {
            AsmConvertOrganizer.process(asmBuilder,regGetter,funcSymbol, irBlock);
        }

        //asmBuilder.mov(Regs.R0, 5); //todo 返回个5，测试用,记得删

        genFunctionGenericEnd(asmBuilder, funcSymbol);

        return asmBuilder.getBuild();
    }

    //现场保护等
    public void genFunctionGenericStart(AsmBuilder asmBuilder, FuncSymbol funcSymbol) {

        if (funcSymbol.hasFuncCallInside()) {
            asmBuilder.push(new Reg[]{Regs.FP}, true).add(Regs.FP, Regs.SP, 4).sub(Regs.SP, Regs.SP,
                                                                                  funcSymbol.getFrameSize()+4);
        } else {
            //str fp,[sp,#-4]!
            //add fp,sp,#0,
            //sub sp,sp,#20
            asmBuilder.mem(AsmBuilder.Mem.STR, null, Regs.FP, Regs.SP, -4, true, false);
            asmBuilder.add(Regs.FP, Regs.SP, 0);
            asmBuilder.sub(Regs.SP, Regs.SP, funcSymbol.getFrameSize() + 4);
        }
    }

    //函数返回之类的指令
    public void genFunctionGenericEnd(AsmBuilder asmBuilder, FuncSymbol funcSymbol) {

        asmBuilder.label(funcSymbol.getAsmEndLabel());
        if (funcSymbol.hasFuncCallInside()) {
            asmBuilder.sub(Regs.SP, Regs.FP, 4).pop(new Reg[]{Regs.FP}, true);
        } else {
            //恢复sp,fp,跳转回调用处
            asmBuilder.add(Regs.SP, Regs.FP, 0).mem(AsmBuilder.Mem.LDR, null, Regs.FP, Regs.SP, 4, false, true).bx(
                    Regs.LR);
        }
    }

    public List<AsmSection> genStaticData() {
        List<AsmSection> sections = new ArrayList<>();
        for (SymbolTable table : symbolTableHost.symbolTableMap.values()) {
            SymbolDomain domain = table.getDomain();
            FuncSymbol funcSymbol = domain.getFunc();
            for (ValueSymbol symbol : table.getAllSymbol()) {
                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    if (!varSymbol.hasConstInitValue) {
                        continue;
                    }

                    AsmBuilder builder = new AsmBuilder();
                    String label = String.format(".L%s.%d.%s", funcSymbol == null ? "" : funcSymbol.getAsmLabel(),
                                                 domain.getId(), varSymbol.symbolToken.getText());

                    varSymbol.asmDataLabel = label;

                    builder.type(label, AsmBuilder.Type.Object).data().global(label).align(2).label(label);

                    for (int initValue : varSymbol.initValues) {
                        builder.word(initValue);
                    }
                    builder.size(label, varSymbol.getByteSize());

                    sections.add(builder.getBuild());
                } else if (symbol instanceof ConstSymbol) {

                }
            }
        }

        return sections;
    }

    /**
     * 将中间代码划分为块
     */
    public List<IRBlock> divideIntoBlock(IRFunction irFunction) {
        List<IRBlock> result = new ArrayList<>();

        FuncSymbol funcSymbol = irFunction.funcSymbol;
        List<InterRepresent> codes = irFunction.flatIR();
        Set<InterRepresent> enterPoints = new HashSet<>();
        int len = codes.size();
        // 第一条语句
        enterPoints.add(codes.get(0));
        int labelID = 0;

        for (int i = 1; i < len; i++) {
            InterRepresent ir = codes.get(i);
            if (ir instanceof GotoRepresent) {
                // goto语句的下一条语句
                enterPoints.add(codes.get(i + 1));
                // 目标语句
                enterPoints.add(((GotoRepresent) ir).getTargetIR());

                String label;
                if (ir.hasLabel()) label = ir.getLabel();
                else {
                    label = String.format("%s.%d", funcSymbol.funcName.getText(), labelID++);
                }
                ((GotoRepresent) ir).getTargetIR().setLabel(label);
            }
        }

        for (int i = 0; i < codes.size(); i++) {
            InterRepresent ir = codes.get(i);

            if (enterPoints.contains(ir)) {



                IRBlock block = new IRBlock(ir.getLabel());
                block.addIR(ir);
                while (++i < len && !enterPoints.contains(codes.get(i))) {
                    // TODO 还未考虑halt
                    block.addIR(codes.get(i));
                }
                i--;

                result.add(block);
            }
        }

        return result;
    }

    /**
     * 计算变量的下次引用与活跃度
     */
    public void calNextRef(List<IRBlock> irBlocks) {
        for (IRBlock block : irBlocks) {
            Map<Address, Reference> refTable = new HashMap<>();
            for (int i = block.irs.size() - 1; i >= 0; i--) {

                InterRepresent ir = block.irs.get(i);

                ir.getAllAddress().forEach(var -> {
                    Reference ref = refTable.getOrDefault(var, new Reference(null, true));
                    // a & c
                    ir.refMap.put(var, ref);
                    if (var.isLVal) {
                        // b
                        refTable.put(var, new Reference(null, false));
                    } else {
                        // d
                        refTable.put(var, new Reference(ir, true));
                    }
                });


                /*Util.traverseAddress(ir, var -> {
                    Reference ref = refTable.getOrDefault(var, new Reference(null, true));
                    // a & c
                    ir.refMap.put(var, ref);
                    if (var.isLVal) {
                        // b
                        refTable.put(var, new Reference(null, false));
                    } else {
                        // d
                        refTable.put(var, new Reference(ir, true));
                    }
                });*/
            }
        }
    }
}
