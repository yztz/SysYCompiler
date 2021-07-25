package compiler.asm;

import compiler.asm.converter.AsmConvertOrganizer;
import compiler.genir.AbstractIR;
import compiler.genir.IRBlock;
import compiler.genir.IRFunction;
import compiler.genir.IRUnion;
import compiler.genir.code.GotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.*;

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

                AsmSection dataSection = genFunctionData(((IRFunction) ir).funcSymbol);
                AsmSection funcSection = genFunction((IRFunction) ir);

                funcSection.getText(builder);
                dataSection.getText(builder);
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

        List<IRBlock> irBlocks = divideIntoBlock(irFunction);
        calNextRef(irBlocks);

        RegGetter regGetter = new RegGetterImpl();
        // todo 汇编代码生成

        for (IRBlock irBlock : irBlocks) {
            AsmConvertOrganizer.process(asmBuilder,regGetter,funcSymbol, irBlock);
        }

        AsmSection midSection = asmBuilder.startNew();
        //开始新的AsmSection,生成函数的开头
        //把开头放在这里生成，是因为我们需要知道代码中是否修改了lr寄存器
        asmBuilder.text().align(2).global().arch("armv7-a").fpu("vfp").type(AsmBuilder.Type.Function).label();
        genFunctionGenericStart(asmBuilder, funcSymbol,regGetter);
        AsmSection headSection = asmBuilder.startNew();

        asmBuilder.setSection(midSection); //继续之前的代码，生成结尾
        genFunctionGenericEnd(asmBuilder, funcSymbol);

        //开头加上后边的部分，生成完整的代码
        headSection.add(midSection);
        return headSection;
    }

    //现场保护等
    public void genFunctionGenericStart(AsmBuilder asmBuilder, FuncSymbol funcSymbol,RegGetter regGetter) {

        if (asmBuilder.isLrModified()) {
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

        for (int i = 0; i < Math.min(4,funcSymbol.getParamNum()); i++) {
            asmBuilder.str(Regs.REGS[i],Regs.FP,Util.getSymbolOffsetFp(funcSymbol.paramSymbols.get(i)));
        }
        for(int i = funcSymbol.getParamNum()-1;i>=4;i--)
        {
            Reg tmp = regGetter.getTmpRegister();
            asmBuilder.ldr(tmp,Regs.FP,Util.getParamOffsetCalledFp(i));
            asmBuilder.str(tmp,Regs.FP,Util.getSymbolOffsetFp(funcSymbol.paramSymbols.get(i)));
        }
    }

    //函数返回之类的指令
    public void genFunctionGenericEnd(AsmBuilder asmBuilder, FuncSymbol funcSymbol) {

        asmBuilder.label(funcSymbol.getAsmEndLabel());
        if (asmBuilder.isLrModified()) {
            asmBuilder.sub(Regs.SP, Regs.FP, 4).pop(new Reg[]{Regs.FP}, true);
        } else {
            //恢复sp,fp,跳转回调用处
            asmBuilder.add(Regs.SP, Regs.FP, 0).mem(AsmBuilder.Mem.LDR, null, Regs.FP, Regs.SP, 4, false, true).bx(
                    Regs.LR);
        }
    }

    public AsmSection genFunctionData(FuncSymbol funcSymbol)
    {
        AsmBuilder builder = new AsmBuilder(Util.getFuncDataLabel(funcSymbol));
        builder.align(2);
        builder.label();

        for (SymbolTable table : symbolTableHost.symbolTableMap.values()) {
            SymbolDomain domain = table.getDomain();

            if(funcSymbol!=domain.getFunc())
                continue;
            int indexInFunc = 0;
            for (ValueSymbol symbol : table.getAllSymbol()) {
                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    if(Util.isNeedInitInDataSection(varSymbol))
                    {
                        builder.word(Util.getVarLabel(funcSymbol,domain,varSymbol));
                        varSymbol.indexInFuncData = indexInFunc++;
                    }else if(varSymbol.initValues!=null && varSymbol.initValues.length>0){
                        builder.word(varSymbol.initValues[0]);
                        varSymbol.indexInFuncData = indexInFunc++;
                    }

                } else if (symbol instanceof ConstSymbol) {

                }
            }
        }

        return builder.getSection();
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
                    String label = Util.getVarLabel(funcSymbol,domain,varSymbol);

                    varSymbol.asmDataLabel = label;

                    builder.type(label, AsmBuilder.Type.Object).data().global(label).align(2).label(label);

                    for (int initValue : varSymbol.initValues) {
                        builder.word(initValue);
                    }
                    builder.size(label, varSymbol.getByteSize());

                    sections.add(builder.getSection());
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