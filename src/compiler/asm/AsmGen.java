package compiler.asm;

import compiler.ConstDef;
import compiler.Util;
import compiler.asm.converter.AsmConvertOrganizer;
import compiler.genir.IRCollection;
import compiler.genir.IRBlock;
import compiler.genir.IRFunction;
import compiler.genir.IRUnion;
import compiler.genir.code.*;
import compiler.optim.OptimizeProcessor;
import compiler.symboltable.*;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.initvalue.InitValue;

import java.util.*;

public class AsmGen {
    SymbolTableHost symbolTableHost;
    public boolean genDebugInfo = false;
    public AsmGen(SymbolTableHost symbolTableHost) {
        //throw new IOException("试一试");
        this.symbolTableHost = symbolTableHost;
    }

    public String generate(IRUnion irUnion) {
        StringBuilder builder = new StringBuilder();
        List<AsmSection> staticDataSection = null;
        try {
            staticDataSection = genStaticData();
        }catch (NullPointerException ne)
        {
            Util.printStackAndExit(-11,ne);
        }catch (Exception e)
        {
            Util.printStackAndExit(-12,e);
        }
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
        try {
            prepareInformation(funcSymbol,irFunction);
        }catch (NullPointerException ne)
        {
            Util.printStackAndExit(-13,ne);
        }catch (Exception e)
        {
            Util.printStackAndExit(-14,e);
        }

        List<IRBlock> irBlocks = OptimizeProcessor.optimize(irFunction);

        RegGetter regGetter = null;
        try {
            regGetter = new RegGetter(irBlocks);
        }catch (NullPointerException ne)
        {
            Util.printStackAndExit(-19,ne);
        }catch (Exception e)
        {
            Util.printStackAndExit(-20,e);
        }

        List<AsmSection> codeSections = new LinkedList<>();
        try {
            codeSections.addAll(AsmConvertOrganizer.process(regGetter,funcSymbol, irBlocks, genDebugInfo));
        }catch (NullPointerException ne)
        {
            Util.printStackAndExit(-21,ne);
        }catch (ArrayIndexOutOfBoundsException e)
        {
            Util.printStackAndExit(-22,e);
        }catch (Exception e)
        {
            Util.printStackAndExit(-23,e);
        }

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
        for (InterRepresent ir : irFunction.getAllIR()) {
            if(ir instanceof CallRepresent)
            {
                if(((CallRepresent) ir).params!=null)
                    maxCallParamNum = Math.max(maxCallParamNum,((CallRepresent) ir).params.length);
            }
        }
        funcSymbol.localByteSize = totalByteSize;
        funcSymbol.maxCallParamCount = maxCallParamNum;
        //funcSymbol.setStackFrameSize(AsmUtil.getFrameSize(funcSymbol));
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
}
