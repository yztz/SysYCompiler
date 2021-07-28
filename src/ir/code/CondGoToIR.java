package ir.code;

import ast.IAstValue;
import common.ILabel;
import common.OP;
import asm.IName;

import java.util.function.Consumer;

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
    public void traverseRVal(Consumer<IName> handler) {
        if (left instanceof IName) handler.accept((IName) left);
        if (right instanceof IName) handler.accept((IName) right);
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s %s %s goto %-5s", getLabelName(), left, relOP, right, target);
    }
}
