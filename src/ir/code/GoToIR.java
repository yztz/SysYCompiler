package ir.code;

import common.ILabel;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GoToIR extends IR{
    public GoToIR(ILabel target) {
        super(OP.GOTO);
        this.op1 = target;
    }

    public ILabel getTarget() {
        return (ILabel) op1;
    }


    @Override
    public IName getLVal() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-10s%-5s", getLabelName(), "goto", op1);
    }
}
