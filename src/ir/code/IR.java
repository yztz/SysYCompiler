package ir.code;

import ast.IAstValue;
import common.ILabel;
import common.OP;

import java.util.Objects;

public class IR {
    public ILabel label;
    public OP op;
    public IAstValue op1;
    public IAstValue op2;
    public IAstValue op3;

    public IR(OP op) {
        this.op = op;
    }

    public void setLabel(ILabel label) {
        this.label = label;
    }

    public String getLabelName() {
        if (null == label) return "";
        return label.getLabelName();
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-10s%-10s%-10s%-10s",
                getLabelName(),
                op,
                Objects.toString(op1, ""),
                Objects.toString(op2, ""),
                Objects.toString(op3, ""));
    }
}
