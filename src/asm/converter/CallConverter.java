package asm.converter;

import asm.AsmBuilder;
import asm.Reg;
import asm.RegGetter;
import asm.Regs;
import genir.code.AddressOrData;
import genir.code.CallRepresent;
import genir.code.InterRepresent;
import symboltable.FuncSymbol;

import java.util.Collection;

public class CallConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof CallRepresent;
    }

    @Override
    public void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        CallRepresent callIr = (CallRepresent) ir;
        FuncSymbol targetFun = callIr.funcSymbol;

        if(callIr.params!=null)
        {
            int paramStackLen = callIr.params.length - 4;
            int stackOffset = 0;
            for (int i = callIr.params.length-1; i>=4; i--) {
                AddressOrData param = callIr.params[i];
                Reg rd;
                if (param.isData) {
                    rd = regGetter.getTmpRegister();
                    builder.mov(rd,param.item);
                }else{
                    rd = param.reg;
                }
                builder.str(rd,Regs.SP,(paramStackLen - stackOffset - 1) *4);
                stackOffset++;
            }

            // 通过寄存器传参
            for (int i = 0; i < Math.min(callIr.params.length,4); i++) {
                AddressOrData param = callIr.params[i];
                if (param.isData) {
                    builder.mov(Regs.REGS[i],param.item);
                }else{
                    builder.mov(Regs.REGS[i],param.reg);
                }
            }
        }



        builder.bl(targetFun.getAsmLabel());
        if (callIr.returnResult != null) {
            callIr.returnResult.reg = Regs.R0;
        }
    }
}
