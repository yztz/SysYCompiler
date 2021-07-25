package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.Reg;
import compiler.asm.RegGetter;
import compiler.genir.code.IfGotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class IfGotoConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof IfGotoRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
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

        return 1;
    }
}
