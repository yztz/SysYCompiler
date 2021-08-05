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

    public CondGoToIR(OP op, ILabel target, IAstValue left, IAstValue right) {
        super(op);
        this.op1 = target;
        this.op2 = left;
        this.op3 = right;
        if (this.op2 instanceof Immediate) {
            this.op2 = right;
            this.op3 = left;
        }
    }


    @Override
    public IName getLVal() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s %s %s goto %-5s", getLabelName(), op2, op, op3, op1);
    }
}
