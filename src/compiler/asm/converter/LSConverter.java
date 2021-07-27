package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.asm.operand.ShiftOp;
import compiler.genir.code.LSRepresent;
import compiler.symboltable.ParamSymbol;
import compiler.symboltable.ValueSymbol;

public abstract class LSConverter extends AsmConverter {


    public int process(AsmBuilder.Mem op,AsmBuilder builder, RegGetter regGetter, LSRepresent ir) {
        ValueSymbol valueSymbol = ir.valueSymbol;

        Reg targetReg = regGetter.getReg(ir, ir.target);

        if(!(valueSymbol instanceof ParamSymbol) || !valueSymbol.isArray())
        {
            if(ir.offset==null || ir.offset.isData)
            {
                int offsetFP = 0;
                if(ir.offset==null)
                    offsetFP =Util.getSymbolOffsetFp(valueSymbol);
                else
                    offsetFP =Util.getSymbolOffsetFp(valueSymbol, ir.offset.item);

                builder.mem(op, null, targetReg, Regs.FP,
                            offsetFP, false, false);
            }else{

                int offsetFPWord = Util.getSymbolOffsetFp(valueSymbol)/ ConstDef.WORD_SIZE;
                Reg rm = regGetter.getReg(ir,ir.offset);
                builder.add(rm,rm,offsetFPWord);
                builder.mem(op, null, targetReg, Regs.FP,
                            rm, false, ShiftOp.LSL, 2, false, false);
            }
        }else{//是函数参数，并且是数组，此时栈里保存的是地址

            if(ir.offset==null || ir.offset.isData)
            {
                int offsetFP = Util.getSymbolOffsetFp(valueSymbol);

                Reg tmp = regGetter.getTmpRegister();
                builder.ldr(tmp,Regs.FP,offsetFP); // 先读取地址

                int offsetArray = 0;
                if(ir.offset!=null && ir.offset.item!=0)
                {
                    offsetArray = ir.offset.item*ConstDef.WORD_SIZE;
                }
                builder.mem(op, null, targetReg, tmp,
                            offsetArray, false, false);
            }else{
                int offsetFP = Util.getSymbolOffsetFp(valueSymbol);
                Reg tmp = regGetter.getTmpRegister();
                builder.ldr(tmp,Regs.FP,offsetFP); // 先读取地址

                Reg rm = regGetter.getReg(ir,ir.offset);

                builder.mem(op, null, targetReg, tmp,
                            rm, false, ShiftOp.LSL, 2, false, false);
            }
        }





        return 1;
    }
}
