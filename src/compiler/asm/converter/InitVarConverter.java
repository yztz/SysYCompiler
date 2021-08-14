package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.genir.code.InitVarRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.VarSymbol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InitVarConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof InitVarRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        InitVarRepresent initIR = (InitVarRepresent) ir;
        VarSymbol varSymbol = initIR.varSymbol;

        int offsetInFuncData = dataHolder.getIndexInFuncData(varSymbol)* ConstDef.WORD_SIZE;
        //String funcDataLabel = getFuncDataLabel(funcSymbol);

        if(AsmUtil.isNeedInitInDataSection(varSymbol))//数据在数据区,funcData里保存的是地址
        {

            if (varSymbol.initValues.getLength() > 4) //大于4个，用memcpy
            {
                /*Reg src = regGetter.getTmpRegister(0);
                Reg des = regGetter.getTmpRegister(1);*/
                builder.sub(Regs.R0, Regs.FP, -AsmUtil.getSymbolOffsetFp(varSymbol));
                builder.ldr(Regs.R1, dataHolder.getLabel(), offsetInFuncData);
                builder.mov(Regs.R2, varSymbol.getByteSize());

                List<Reg> usingRegister = regGetter.getUsingRegNext().stream().filter(r -> {
                    int id = r.getId();
                    return id > 2;
                }).sorted(Comparator.comparingInt(Reg::getId)).collect(Collectors.toList());

                AsmUtil.protectRegs(builder, regGetter, usingRegister);

                builder.bl("memcpy");

                AsmUtil.recoverRegs(builder, regGetter, usingRegister);
            } else { //小于4个，用str加载

                Reg addr = regGetter.getTmpRegister(0);
                builder.ldr(addr, dataHolder.getLabel(), offsetInFuncData);

                Reg[] regs = new Reg[(int) varSymbol.initValues.getLength()];
                for (int i = 0; i < regs.length; i++) {
                    regs[i] = regGetter.getTmpRegister(i + 1);
                }
                builder.ldm(AsmBuilder.LSAddressMode.NONE, addr, Arrays.stream(regs).collect(Collectors.toList()));
                builder.sub(addr, Regs.FP, -AsmUtil.getSymbolOffsetFp(varSymbol));
                builder.stm(AsmBuilder.LSAddressMode.NONE, addr, Arrays.stream(regs).collect(Collectors.toList()));
            }
        }else if(AsmUtil.isNeedInitInFuncData(varSymbol)){//不是数组

            Reg tmp = regGetter.getTmpRegister();
            builder.ldr(tmp,dataHolder.getLabel(),offsetInFuncData);
            builder.str(tmp, Regs.FP, AsmUtil.getSymbolOffsetFp(varSymbol));

        }else if(varSymbol.hasConstInitValue){
            int initVal = varSymbol.initValues.get(0);
            Reg tmp = regGetter.getTmpRegister();
            builder.mov(tmp,initVal);
            builder.str(tmp, Regs.FP, AsmUtil.getSymbolOffsetFp(varSymbol));
        }

        return 1;
    }
}
