package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.asm.operand.ImmOperand;
import compiler.asm.operand.RegShiftImmOperand;
import compiler.asm.operand.ShiftOp;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LAddrRepresent;
import compiler.symboltable.HasInitSymbol;
import compiler.symboltable.ParamSymbol;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;
import java.util.List;

public class LAddrConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof LAddrRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        LAddrRepresent retIr = (LAddrRepresent) ir;
        Reg rd = regGetter.getReg(retIr, retIr.target);
        ValueSymbol symbol = retIr.valueSymbol;
        if(symbol instanceof HasInitSymbol && ((HasInitSymbol)symbol).isGlobalSymbol())
        {
            int offsetInFuncData = dataHolder.getIndexInFuncData(symbol)* ConstDef.WORD_SIZE;
            builder.ldr(rd, dataHolder.getLabel(), offsetInFuncData);
            if(retIr.offset.isData && retIr.offset.item!=0)
            {
                builder.add(rd,rd,new ImmOperand(retIr.offset.item*ConstDef.WORD_SIZE));
            }
        }else if(symbol instanceof ParamSymbol){ //todo 这么处理好吗
            long offsetFP = AsmUtil.getSymbolOffsetFp(symbol);
            if(retIr.offset.isData)
            {
                offsetFP+=(retIr.offset.item*ConstDef.WORD_SIZE);
            }
            builder.ldr(rd,Regs.FP,offsetFP); // 读取地址
        }else{
            long symbolOffsetFp = AsmUtil.getSymbolOffsetFp(symbol);
            if(retIr.offset.isData)
            {
                symbolOffsetFp+=(retIr.offset.item*ConstDef.WORD_SIZE);
            }
            builder.add(rd,Regs.FP,symbolOffsetFp);

        }
        if(!retIr.offset.isData)
        {
            builder.add(rd,rd,new RegShiftImmOperand(ShiftOp.LSL,
                                                     regGetter.getReg(retIr,retIr.offset),2));
        }

        return 1;
    }
}
