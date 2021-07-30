package compiler.asm.converter;

import compiler.asm.*;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.CallRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;

public class CallConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof CallRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        CallRepresent callIr = (CallRepresent) ir;
        AbstractFuncSymbol targetFun = callIr.funcSymbol;

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
                    rd = regGetter.getReg(ir,param);
                }
                builder.str(rd,Regs.SP,(paramStackLen - stackOffset - 1) *4);
                stackOffset++;
            }

            /*Map<Reg,Reg> regRemap = new HashMap<>();
            for (int i = 0; i < Math.min(callIr.params.length,4); i++) {
                AddressOrData param = callIr.params[i];
                if (!param.isData) {
                    regRemap.put(param.reg,param.reg);
                }
            }*/


            // 通过寄存器传参
            for (int i = 0; i < Math.min(callIr.params.length,4); i++) {
                AddressOrData param = callIr.params[i];
                if (param.isData) {
                    /*if(!regRemap.containsKey(Regs.REGS[i])){ //如果被其他参数占用
                        // todo 如果r0,r1,r2,r3之后还会用到，怎么办
                    }*/
                    builder.mov(Regs.REGS[i],param.item);
                }else{
                    builder.mov(Regs.REGS[i],regGetter.getReg(ir,param));
                }
            }
        }



        builder.bl(targetFun instanceof FuncSymbol ?((FuncSymbol)targetFun).getAsmLabel():
                           targetFun.getFuncName());
        if (callIr.returnResult != null) {
            regGetter.setReg(callIr,callIr.returnResult,Regs.R0);
        }

        return 1;
    }
}
