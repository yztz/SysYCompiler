package ir.code;

import ast.IAstValue;
import ast.Immediate;
import common.ILabel;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CondGoToIR extends IR{

    public CondGoToIR(OP cond, ILabel target, IAstValue left, IAstValue right) {
        super(cond);
        this.op1 = target;
        if (this.op2 instanceof Immediate) {
            this.op2 = right;
            this.op3 = left;
            if (op == OP.LE_GOTO) this.op = OP.GT_GOTO;
            if (op == OP.LT_GOTO) this.op = OP.GE_GOTO;
            if (op == OP.GE_GOTO) this.op = OP.LT_GOTO;
            if (op == OP.GT_GOTO) this.op = OP.LE_GOTO;
        } else {
            this.op2 = left;
            this.op3 = right;
        }
    }


    @Override
    public IName getLVal() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s %s %s goto %-5s", getLabelName(), op2, op, op3,op1);
    }
}
