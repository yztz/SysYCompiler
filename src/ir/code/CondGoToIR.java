package ir.code;

import ast.AstValue;
import ast.OP;

public class CondGoToIR extends GoToIR{
    public AstValue cond;


    public CondGoToIR(Label target, AstValue cond) {
        super(target);
        this.cond = cond;
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s goto %-5s", getLabelName(), cond, target);
    }
}
