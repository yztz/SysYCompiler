package ir.code;

import ast.IAstValue;
import common.ILabel;
import common.Label;
import common.OP;

public class CondGoToIR extends GoToIR{
    public IAstValue left;
    public OP op;
    public IAstValue right;


    public CondGoToIR(ILabel target, IAstValue left, OP op, IAstValue right) {
        super(target);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s %s %s goto %-5s", getLabelName(), left, op, right, target);
    }
}
