package asm.converter;

import asm.AsmBuilder;
import asm.Reg;
import asm.RegGetter;
import genir.code.IfGotoRepresent;
import genir.code.InterRepresent;
import symboltable.FuncSymbol;

import java.util.Collection;

public class IfGotoConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof IfGotoRepresent;
    }

    @Override
    public void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        IfGotoRepresent ifIr = (IfGotoRepresent) ir;

        AsmBuilder.CondB cond = null;

        IfGotoRepresent.RelOp relOp = ifIr.relOp;

        if(!ifIr.left.isData && !ifIr.right.isData)
        {
            Reg rd = ifIr.left.reg;
            Reg rn = ifIr.right.reg;
            builder.cmp(rd,rn);
        }else if(!ifIr.left.isData){
            Reg rd = ifIr.left.reg;
            int imm8m = ifIr.right.item;
            builder.cmp(rd,imm8m);
        }else {
            Reg rd = ifIr.right.reg;
            int imm8m = ifIr.left.item;
            builder.cmp(rd,imm8m);
            relOp = relOp.getReverse(); //左右颠倒
        }

        switch (relOp)
        {

            case LESS:
                cond = AsmBuilder.CondB.BLT;
                break;
            case GREATER:
                cond = AsmBuilder.CondB.BGT;
                break;
            case LESS_EQUAL:
                cond = AsmBuilder.CondB.BLE;
                break;
            case GREATER_EQUAL:
                cond = AsmBuilder.CondB.BGE;
                break;
            case EQUAL:
                cond = AsmBuilder.CondB.BEQ;
                break;
            case NOT_EQUAL:
                cond = AsmBuilder.CondB.BNE;
                break;
        }


        builder.bxx(cond,ifIr.getTargetIR().getLabel());
    }
}
