package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.genir.code.InitVarRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.VarSymbol;

import java.util.Arrays;
import java.util.Collection;
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
        String funcDataLabel = AsmUtil.getFuncDataLabel(funcSymbol);

        if(AsmUtil.isNeedInitInDataSection(varSymbol))//数据在数据区,funcData里保存的是地址
        {

            if(varSymbol.initValues.length>4) //大于4个，用memcpy
            {
                /*Reg src = regGetter.getTmpRegister(0);
                Reg des = regGetter.getTmpRegister(1);*/
                builder.sub(Regs.R0, Regs.FP,-AsmUtil.getSymbolOffsetFp(varSymbol));
                builder.ldr(Regs.R1,funcDataLabel,offsetInFuncData);
                builder.mov(Regs.R2,varSymbol.getByteSize());
                builder.bl("memcpy");
            }else{ //小于4个，用sdr加载

                Reg addr = regGetter.getTmpRegister(0);
                builder.ldr(addr,funcDataLabel,offsetInFuncData);

                Reg[] regs = new Reg[varSymbol.initValues.length];
                for (int i = 0; i < regs.length; i++) {
                    regs[i] = regGetter.getTmpRegister(i+1);
                }
                builder.ldm(AsmBuilder.LSAddressMode.NONE, addr, Arrays.stream(regs).collect(Collectors.toList()));
                builder.sub(addr, Regs.FP, -AsmUtil.getSymbolOffsetFp(varSymbol));
                builder.stm(AsmBuilder.LSAddressMode.NONE, addr , Arrays.stream(regs).collect(Collectors.toList()));
            }
        }else{ //不是数组
            Reg tmp = regGetter.getTmpRegister();
            builder.ldr(tmp,funcDataLabel,offsetInFuncData);
            builder.sdr(tmp, Regs.FP, AsmUtil.getSymbolOffsetFp(varSymbol));
        }

        return 1;
    }
}
