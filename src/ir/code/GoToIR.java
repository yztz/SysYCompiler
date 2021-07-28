package ir.code;

import common.ILabel;
import common.OP;
import asm.IName;

import java.util.function.Consumer;

public class GoToIR extends IR{
    public ILabel target;
    public GoToIR(ILabel target) {
        super(OP.GOTO);
        this.target = target;
    }

    @Override
    public void traverseLVal(Consumer<IName> handler) {

    }

    @Override
    public void traverseRVal(Consumer<IName> handler) {

    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-10s%-5s", getLabelName(), "goto", target);
    }
}
