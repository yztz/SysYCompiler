package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.FunctionDataHolder;
import compiler.asm.Reg;
import compiler.asm.RegGetter;
import compiler.genir.code.IfGotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.RelRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;

public class RelConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof RelRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        RelRepresent relIr = (RelRepresent) ir;
        Reg rd = regGetter.getReg(relIr,relIr.target);
        if(relIr.left.isData && relIr.right.isData)
        {
            int result = relIr.op.compute(relIr.left.item,relIr.right.item)?1:0;
            builder.mov(rd,result);
        }else if(relIr.left.isData)
        {
            /*Reg left = regGetter.getTmpRegister();
            builder.mov(left,relIr.left.item);*/
            Reg right = regGetter.getReg(relIr, relIr.right);
            builder.cmp(right,relIr.left.item); //左右交换了
            IfGotoRepresent.RelOp op = relIr.op.swapLeftRight();

            builder.mov(AsmBuilder.Cond.getFromRelOP(op), rd, 1);
            builder.mov(AsmBuilder.Cond.getFromRelOP(op.not()), rd, 0);
        }else if(relIr.right.isData)
        {
            Reg left = regGetter.getReg(relIr, relIr.left);
            builder.cmp(left,relIr.right.item);
            IfGotoRepresent.RelOp op = relIr.op;

            builder.mov(AsmBuilder.Cond.getFromRelOP(op), rd, 1);
            builder.mov(AsmBuilder.Cond.getFromRelOP(op.not()), rd, 0);
        }else{ //都不是立即数
            Reg left = regGetter.getReg(relIr, relIr.left);
            Reg right = regGetter.getReg(relIr, relIr.right);
            builder.cmp(left,right);
            IfGotoRepresent.RelOp op = relIr.op;

            builder.mov(AsmBuilder.Cond.getFromRelOP(op), rd, 1);
            builder.mov(AsmBuilder.Cond.getFromRelOP(op.not()), rd, 0);
        }

        return 1;
    }
}
