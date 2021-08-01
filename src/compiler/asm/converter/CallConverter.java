package compiler.asm.converter;

import compiler.asm.*;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.CallRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;
import java.util.stream.Collectors;

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
                    regGetter.releaseReg(rd);
                }else{
                    rd = regGetter.getReg(ir,param);
                }
                builder.str(rd,Regs.SP,(paramStackLen - stackOffset - 1) *4);
                regGetter.releaseRegIfNoRef(rd);
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
                    Reg reg = regGetter.getReg(ir, param);
                    builder.mov(Regs.REGS[i], reg);
                    regGetter.releaseRegIfNoRef(reg);
                }
            }
        }


        List<Reg> usingRegister =
                regGetter.getUsingRegNext().stream().filter(r->{
                    int id = r.getId();
                    return id>3;
                }).sorted(Comparator.comparingInt(Reg::getId)).collect(Collectors.toList());

        if(usingRegister.size()>0)
        {
            Reg tmp = regGetter.getTmpRegister();
            //dataHolder.loadFromFuncData(builder, FunctionDataHolder.RegFuncData.getInstance(),tmp);
            builder.add(tmp,Regs.FP,AsmUtil.getRegOffsetFP());
            builder.stm(AsmBuilder.LSAddressMode.NONE,tmp,usingRegister);
        }

        builder.bl(targetFun instanceof FuncSymbol ?((FuncSymbol)targetFun).getAsmLabel():
                           targetFun.getFuncName());

        if(usingRegister.size()>0)
        {
            Reg tmp = regGetter.getTmpRegister();
            //dataHolder.loadFromFuncData(builder, FunctionDataHolder.RegFuncData.getInstance(),tmp);
            builder.add(tmp,Regs.FP,AsmUtil.getRegOffsetFP());
            builder.ldm(AsmBuilder.LSAddressMode.NONE,tmp,usingRegister);
        }

        if (callIr.returnResult != null) {
            Reg result = regGetter.getReg(callIr,callIr.returnResult);
            builder.mov(result,Regs.R0);
            //regGetter.setReg(callIr,callIr.returnResult,Regs.R0);
        }

        return 1;
    }
}
