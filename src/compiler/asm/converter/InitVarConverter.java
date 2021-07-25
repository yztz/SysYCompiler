package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.genir.code.InitVarRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.FuncSymbol;
import compiler.symboltable.VarSymbol;

import java.util.Collection;
import java.util.List;

public class InitVarConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof InitVarRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        InitVarRepresent initIR = (InitVarRepresent) ir;
        VarSymbol varSymbol = initIR.varSymbol;

        int offsetInFuncData = varSymbol.indexInFuncData* ConstDef.WORD_SIZE;
        String funcDataLabel = Util.getFuncDataLabel(funcSymbol);

        if(Util.isNeedInitInDataSection(varSymbol))//数据在数据区,funcData里保存的是地址
        {

            if(varSymbol.initValues.length>4) //大于4个，用memcpy
            {
                /*Reg src = regGetter.getTmpRegister(0);
                Reg des = regGetter.getTmpRegister(1);*/
                builder.ldr(Regs.R0,funcDataLabel,offsetInFuncData);
                builder.sub(Regs.R1, Regs.FP,-Util.getSymbolOffsetFp(varSymbol));
                builder.mov(Regs.R2,varSymbol.getByteSize());
                builder.bl("memcpy");
            }else{ //小于4个，用sdr加载

                Reg addr = regGetter.getTmpRegister(0);
                builder.ldr(addr,funcDataLabel,offsetInFuncData);

                Reg[] regs = new Reg[varSymbol.initValues.length];
                for (int i = 0; i < regs.length; i++) {
                    regs[i] = regGetter.getTmpRegister(i+1);
                }
                builder.ldm(AsmBuilder.LSAddressMode.NONE, addr, regs);
                builder.sub(addr, Regs.FP, -Util.getSymbolOffsetFp(varSymbol));
                builder.sdm(AsmBuilder.LSAddressMode.NONE,addr ,regs);
            }
        }else{ //不是数组
            Reg tmp = regGetter.getTmpRegister();
            builder.ldr(tmp,funcDataLabel,offsetInFuncData);
            builder.sdr(tmp,Regs.FP,Util.getSymbolOffsetFp(varSymbol));
        }

        return 1;
    }
}
