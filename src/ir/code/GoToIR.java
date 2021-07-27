package ir.code;

import common.ILabel;

public class GoToIR extends IR{
    public ILabel target;
    public GoToIR(ILabel target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-10s%-5s", getLabelName(), "goto", target);
    }
}
