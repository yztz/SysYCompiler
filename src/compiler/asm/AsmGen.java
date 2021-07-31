package compiler.asm;

import compiler.ConstDef;
import compiler.asm.converter.AsmConvertOrganizer;
import compiler.genir.IRCollection;
import compiler.genir.IRBlock;
import compiler.genir.IRFunction;
import compiler.genir.IRUnion;
import compiler.genir.code.*;
import compiler.symboltable.*;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;

public class AsmGen {
    SymbolTableHost symbolTableHost;

    public AsmGen(SymbolTableHost symbolTableHost) {
        this.symbolTableHost = symbolTableHost;
    }

    public String generate(IRUnion irUnion) {
        StringBuilder builder = new StringBuilder();

        List<AsmSection> staticDataSection = genStaticData();
        for (IRCollection ir : irUnion.getAll()) {
            if (ir instanceof IRFunction) {
                IRFunction irFunction = (IRFunction)ir;
                FuncSymbol funcSymbol = irFunction.funcSymbol;
                FunctionDataHolder holder = new FunctionDataHolder(funcSymbol);

                prepareFunctionData(funcSymbol, irFunction, holder);

                AsmSection funcSection = genFunction(irFunction,holder);

                AsmSection dataSection = genFunctionData(funcSymbol, irFunction,holder);

                funcSection.getText(builder);
                dataSection.getText(builder);
            }
        }

        for (AsmSection dataSection : staticDataSection) {
            dataSection.getText(builder);
        }

        return builder.toString();
    }

    public AsmSection genFunction(IRFunction irFunction,FunctionDataHolder holder) {
        FuncSymbol funcSymbol = irFunction.funcSymbol;
        prepareInformation(funcSymbol,irFunction);

        AsmBuilder asmBuilder = new AsmBuilder(funcSymbol.getAsmLabel());

        List<IRBlock> irBlocks = divideIntoBlock(irFunction);

        RegGetter regGetter = new RegGetterImpl(irBlocks);

        // 如果mov的立即数不是imm12，则替换成ldr,改为从内存中加载
        asmBuilder.hookIfNotImmXX(holder, regGetter);
        for (IRBlock irBlock : irBlocks) {
            AsmConvertOrganizer.process(asmBuilder,regGetter,funcSymbol,holder, irBlock);
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
                                                                                  funcSymbol.getStackFrameSize()+4);
        } else {
            //str fp,[sp,#-4]!
            //add fp,sp,#0,
            //sub sp,sp,#20
            asmBuilder.mem(AsmBuilder.Mem.STR, null, Regs.FP, Regs.SP, -4, true, false);
            asmBuilder.add(Regs.FP, Regs.SP, 0);
            asmBuilder.sub(Regs.SP, Regs.SP, funcSymbol.getStackFrameSize() + 4);
        }

        for (int i = 0; i < Math.min(4,funcSymbol.getParamNum()); i++) {
            asmBuilder.str(Regs.REGS[i], Regs.FP, AsmUtil.getSymbolOffsetFp(funcSymbol.paramSymbols.get(i)));
        }
        for(int i = funcSymbol.getParamNum()-1;i>=4;i--)
        {
            Reg tmp = regGetter.getTmpRegister();
            asmBuilder.ldr(tmp, Regs.FP, AsmUtil.getParamOffsetCalledFp(i));
            asmBuilder.str(tmp, Regs.FP, AsmUtil.getSymbolOffsetFp(funcSymbol.paramSymbols.get(i)));
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

    public void prepareInformation(FuncSymbol funcSymbol,IRFunction irFunction)
    {
        int totalByteSize = 0;
        for (SymbolDomain domain : funcSymbol.domains) {
            for (ValueSymbol symbol : domain.symbolTable.getAllSymbol()) {
                totalByteSize+= symbol.getByteSize();
            }
        }
        int maxCallParamNum = 0;
        for (InterRepresent ir : irFunction.flatIR()) {
            if(ir instanceof CallRepresent)
            {
                if(((CallRepresent) ir).params!=null)
                    maxCallParamNum = Math.max(maxCallParamNum,((CallRepresent) ir).params.length);
            }
        }
        int frameSize = ((int) Math.ceil(((double)totalByteSize)/4.0 + maxCallParamNum))*4 + 8;//4字节对齐,直接乘2
        funcSymbol.setStackFrameSize(frameSize);
    }

    public void prepareFunctionData(FuncSymbol funcSymbol,IRFunction irFunction,FunctionDataHolder holder)
    {
        HashSet<ValueSymbol> usedSymbol = new HashSet<>();
        for (InterRepresent ir : irFunction.flatIR()) {
            if(ir instanceof LSRepresent)
            {
                usedSymbol.add(((LSRepresent) ir).valueSymbol);
            }else if(ir instanceof LAddrRepresent)
            {
                usedSymbol.add(((LAddrRepresent) ir).valueSymbol);
            }
        }

        for (ValueSymbol symbol : symbolTableHost.getGlobalSymbolTable().getAllSymbol()) {
            if(!usedSymbol.contains(symbol))
                continue;

            if(symbol instanceof HasInitSymbol)
            {
                HasInitSymbol init = (HasInitSymbol) symbol;
                holder.addData(init);
            }
        }

        for (SymbolTable table : symbolTableHost.symbolTableMap.values()) {
            SymbolDomain domain = table.getDomain();

            if(funcSymbol!=domain.getFunc())
                continue;

            for (ValueSymbol symbol : table.getAllSymbol()) {
                if(symbol instanceof HasInitSymbol)
                {
                    holder.addData(symbol);
                }
            }
        }
        holder.addData(FunctionDataHolder.RegFuncData.getInstance());
    }

    public AsmSection genFunctionData(FuncSymbol funcSymbol,IRFunction irFunction,FunctionDataHolder holder)
    {
        AsmBuilder builder = new AsmBuilder(AsmUtil.getFuncDataLabel(funcSymbol));
        builder.align(2);
        builder.label();
        int indexInFunc = 0;
        for (FunctionDataHolder.FuncData data : holder.getAllFuncData()) {
            data.genData(builder);
            /*if (data instanceof FunctionDataHolder.SymbolFuncData) {
                ValueSymbol symbol = ((FunctionDataHolder.SymbolFuncData) data).symbol;
                if (symbol instanceof HasInitSymbol && ((HasInitSymbol)symbol).isGlobalSymbol()) {
                    HasInitSymbol init = (HasInitSymbol) symbol;
                    builder.word(init.asmDataLabel);
                }else{
                    if (symbol instanceof HasInitSymbol) {
                        HasInitSymbol varSymbol = (HasInitSymbol) symbol;
                        if(AsmUtil.isNeedInitInDataSection(varSymbol))
                        {
                            builder.word(varSymbol.asmDataLabel);
                        }else if(varSymbol.initValues!=null && varSymbol.initValues.length>0){
                            builder.word(varSymbol.initValues[0]);
                        }else{
                            builder.space(ConstDef.WORD_SIZE);
                        }
                    }else{
                        builder.space(ConstDef.WORD_SIZE);
                    }
                }
            }else if(data instanceof FunctionDataHolder.ImmFuncData)
            {
                FunctionDataHolder.ImmFuncData dataItem = (FunctionDataHolder.ImmFuncData) data;
                builder.word(dataItem.imm32);
            }else{
                builder.space(ConstDef.WORD_SIZE);
            }*/
        }

        return builder.getSection();
    }

    public List<AsmSection> genStaticData() {
        List<AsmSection> sections = new ArrayList<>();

        for (ValueSymbol symbol : symbolTableHost.getGlobalSymbolTable().getAllSymbol()) {
            if (symbol instanceof HasInitSymbol) {
                HasInitSymbol varSymbol = (HasInitSymbol) symbol;

                AsmBuilder builder = new AsmBuilder();
                String label = varSymbol.symbolToken.getText();
                varSymbol.asmDataLabel = label;
                if((varSymbol instanceof VarSymbol && ((VarSymbol) varSymbol).hasConstInitValue)
                    || varSymbol instanceof ConstSymbol)
                {
                    buildInitValues(sections, varSymbol, builder, label);
                }else{
                    /*builder.bss().align(2).type(label,AsmBuilder.Type.Object)
                            .size(label, varSymbol.getByteSize()).label(label)
                            .space(varSymbol.getByteSize());*/
                    builder.comm(label, varSymbol.getByteSize());
                    sections.add(builder.getSection());
                }
                /*if (varSymbol instanceof VarSymbol && !((VarSymbol) varSymbol).hasConstInitValue) {
                    continue;
                }*/


            }
        }

        for (SymbolTable table : symbolTableHost.symbolTableMap.values()) {
            SymbolDomain domain = table.getDomain();
            FuncSymbol funcSymbol = domain.getFunc();

            for (ValueSymbol symbol : table.getAllSymbol()) {
                if (symbol instanceof HasInitSymbol) {
                    HasInitSymbol varSymbol = (HasInitSymbol) symbol;

                    if (varSymbol instanceof VarSymbol && !((VarSymbol) varSymbol).hasConstInitValue) {
                        continue;
                    }

                    AsmBuilder builder = new AsmBuilder();
                    String label = AsmUtil.getVarLabel(funcSymbol, domain, varSymbol);

                    buildInitValues(sections, varSymbol, builder, label);
                }
            }
        }

        AsmBuilder builder=new AsmBuilder();
        //builder.align(2).label("reg_addr").word("reg");

        builder.bss().align(2);
        builder.label(FunctionDataHolder.RegFuncData.regDataLabel);
        builder.space(15*ConstDef.WORD_SIZE);
        sections.add(builder.getSection());
        return sections;
    }

    private void buildInitValues(List<AsmSection> sections, HasInitSymbol varSymbol, AsmBuilder builder, String label) {
        varSymbol.asmDataLabel = label;

        builder.type(label, AsmBuilder.Type.Object).data().global(label).align(2).label(label);

        if(varSymbol.initValues!=null && !varSymbol.isAllZero())
        {
            int zeroTailLength = varSymbol.getZeroTailLength();
            int[] initValues = varSymbol.initValues;
            for (int i = 0; i < initValues.length - zeroTailLength; i++) {
                int initValue = initValues[i];
                builder.word(initValue);
            }
            if(zeroTailLength!=0)
                builder.space(zeroTailLength *ConstDef.WORD_SIZE);
        }else{
            builder.space(varSymbol.getByteSize());
        }

        builder.size(label, varSymbol.getByteSize());

        sections.add(builder.getSection());
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
                /*if (ir.hasLabel()) label = ir.getLabel();
                else {*/
                    label = String.format("%s.%d", funcSymbol.getFuncName(), labelID++);
                //}
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
}
