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
import compiler.symboltable.initvalue.InitValue;

import java.util.*;
import java.util.stream.Collectors;

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
                //FunctionDataHolder holder = new FunctionDataHolder(funcSymbol);

                //prepareFunctionData(funcSymbol, irFunction, holder);

                List<AsmSection> funcSections = genFunction(irFunction);
                for (AsmSection section : funcSections) {
                    section.getText(builder);
                }
                //AsmSection dataSection = genFunctionData(funcSymbol, irFunction,holder);

                //funcSection.getText(builder);
                //dataSection.getText(builder);
            }
        }

        for (AsmSection dataSection : staticDataSection) {
            dataSection.getText(builder);
        }

        return builder.toString();
    }

    public List<AsmSection> genFunction(IRFunction irFunction) {
        FuncSymbol funcSymbol = irFunction.funcSymbol;
        prepareInformation(funcSymbol,irFunction);



        List<IRBlock> irBlocks = divideIntoBlock(irFunction);
        optimizeIrOrder(irBlocks);

        RegGetter regGetter = new RegGetter(irBlocks);


        List<AsmSection> codeSections = new LinkedList<>();
        codeSections.addAll(AsmConvertOrganizer.process(regGetter,funcSymbol, irBlocks));


        return codeSections;
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
        funcSymbol.setStackFrameSize(AsmUtil.getFrameSize(totalByteSize,maxCallParamNum));
        /*int frameSize = ((int) Math.ceil(((double)totalByteSize)/4.0 + maxCallParamNum))*4 + 8;//4字节对齐,直接乘2
        funcSymbol.setStackFrameSize(frameSize+AsmUtil.REG_DATA_LEN + AsmUtil.REG_STAGE_LEN);*/
    }

    /*public void prepareFunctionData(FuncSymbol funcSymbol,IRFunction irFunction,FunctionDataHolder holder)
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
        //holder.addData(FunctionDataHolder.RegFuncData.getInstance());
    }*/

   /* public AsmSection genFunctionData(FuncSymbol funcSymbol,IRFunction irFunction,FunctionDataHolder holder)
    {
        AsmBuilder builder = new AsmBuilder(AsmUtil.getFuncDataLabel(funcSymbol));
        builder.align(2);
        builder.label();
        for (FunctionDataHolder.FuncData data : holder.getAllFuncData()) {
            data.genData(builder);
        }

        return builder.getSection();
    }*/


    public List<AsmSection> genStaticData() {
        List<AsmSection> sections = new ArrayList<>();

        for (ValueSymbol symbol : symbolTableHost.getGlobalSymbolTable().getAllSymbol()) {
            if (symbol instanceof HasInitSymbol) {
                HasInitSymbol varSymbol = (HasInitSymbol) symbol;

                AsmBuilder builder = new AsmBuilder();
                String label = varSymbol.symbolToken.getText();
                varSymbol.asmDataLabel = label;
                if((varSymbol instanceof VarSymbol && ((VarSymbol) varSymbol).hasConstInitValue)
                    || varSymbol instanceof ConstSymbol || varSymbol.isGlobalSymbol())
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

                    if (varSymbol instanceof VarSymbol && !((VarSymbol) varSymbol).hasConstInitValue
                        || !AsmUtil.isNeedInitInDataSection(varSymbol)) {
                        continue;
                    }

                    AsmBuilder builder = new AsmBuilder();
                    String label = AsmUtil.getVarLabel(funcSymbol, domain, varSymbol);

                    buildInitValues(sections, varSymbol, builder, label);
                }
            }
        }

        /*AsmBuilder builder=new AsmBuilder();

        builder.bss().align(2);
        builder.label(FunctionDataHolder.RegFuncData.regDataLabel);
        builder.space(FunctionDataHolder.RegFuncData.size);
        sections.add(builder.getSection());*/
        return sections;
    }

    private void buildInitValues(List<AsmSection> sections, HasInitSymbol varSymbol, AsmBuilder builder, String label) {
        varSymbol.asmDataLabel = label;

        builder.type(label, AsmBuilder.Type.Object).data().global(label).align(2).label(label);

        if(varSymbol.initValues!=null && !varSymbol.isAllZero())
        {
            long zeroTailLength = varSymbol.getZeroTailLength();
            InitValue initValues = varSymbol.initValues;
            for (long i = 0; i <= initValues.getLastNonZeroPos(); i++) {
                int initValue = initValues.get(i);
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


    public void optimizeIrOrder(List<IRBlock> irBlocks)
    {
        for (IRBlock irBlock : irBlocks) {
            List<InterRepresent> irs = irBlock.irs;
            HashSet<InterRepresent> irHasMoved=new HashSet<>();
            for (int i = 1; i < irs.size(); i++) {
                InterRepresent ir = irs.get(i);
                if(!(ir instanceof LoadRepresent))
                    continue;

                Collection<AddressRWInfo> allOutput = ir.getAllAddressRWInfo().stream().filter(
                        info->info.isWrite
                ).collect(Collectors.toList());
                if(allOutput.size()==0)
                    continue;

                int j = i+1;
                boolean skip = false;
                for(;j<irs.size();j++)
                {
                    InterRepresent irAfter = irs.get(j);
                    if(irAfter instanceof GotoRepresent)
                    {
                        skip = true;
                        break;
                    }
                    Collection<AddressRWInfo> allUse = irAfter.getAllAddressRWInfo().stream().filter(
                            info->!info.isWrite
                    ).collect(Collectors.toList());
                    if(allUse.size()==0)
                        continue;
                    if(allOutput.stream().anyMatch(allUse::contains))
                    {
                        break;
                    }
                }
                if(skip)
                    continue;
                if(j-i>1 && !irHasMoved.contains(ir))
                {
                    irHasMoved.add(ir);
                    irBlock.moveDown(i, j-1);
                    i--;
                }
            }
        }
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
