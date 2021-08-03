package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.FunctionDataHolder;
import compiler.asm.Reg;
import compiler.asm.RegGetter;
import compiler.genir.code.IfGotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class IfGotoConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof IfGotoRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        IfGotoRepresent ifIr = (IfGotoRepresent) ir;

        AsmBuilder.CondB cond = null;

        IfGotoRepresent.RelOp relOp = ifIr.relOp;

        if(!ifIr.left.isData && !ifIr.right.isData)
        {
            Reg rd = regGetter.getReg(ir,ifIr.left);
            Reg rn = regGetter.getReg(ir,ifIr.right);
            builder.cmp(rd,rn);
        }else if(!ifIr.left.isData ){
            Reg rd = regGetter.getReg(ir,ifIr.left);
            long imm8m = ifIr.right.item;
            builder.cmp(rd,imm8m);
        }else if(!ifIr.right.isData ){
            Reg rd = regGetter.getReg(ir,ifIr.right);
            long imm8m = ifIr.left.item;
            builder.cmp(rd,imm8m);
            relOp = relOp.swapLeftRight(); //左右颠倒
        }else{ //todo 都是立即数
            Reg rd = regGetter.getTmpRegister(0);
            Reg rn = regGetter.getTmpRegister(1);
            builder.mov(rd,ifIr.left.item);
            builder.mov(rn,ifIr.right.item);
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
