package ir.code;

import ast.IAstValue;
import common.Label;

public class CondGoToIR extends GoToIR{
    public IAstValue cond;


    public CondGoToIR(Label target, IAstValue cond) {
        super(target);
        this.cond = cond;
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s goto %-5s", getLabelName(), cond, target);
    }
}
