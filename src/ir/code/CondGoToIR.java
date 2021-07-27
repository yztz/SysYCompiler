package ir.code;

import ast.IAstValue;
import common.ILabel;
import common.Label;
import common.OP;

public class CondGoToIR extends GoToIR{
    public IAstValue left;
    public OP relOP;
    public IAstValue right;


    public CondGoToIR(ILabel target, IAstValue left, OP relOP, IAstValue right) {
        super(target);
        this.left = left;
        this.relOP = relOP;
        this.right = right;
    }

    @Override
    public String toString() {
        System.out.println("print goto");

        return String.format("%-4s\tif %s %s %s goto %-5s", getLabelName(), left, relOP, right, target);
    }
}
