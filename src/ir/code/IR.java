package ir.code;

import ast.IAstValue;
import common.ILabel;
import common.OP;
import common.symbol.Variable;
import ir.IName;
import ir.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class IR {
    public ILabel label;
    public OP op;
    public IAstValue op1;
    public IAstValue op2;
    public IAstValue op3;

    public Map<IName, Reference> refMap = new HashMap<>();

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

    public abstract void traverseLVal(Consumer<IName> handler);
    public abstract void traverseRVal(Consumer<IName> handler);

    public String ref2String() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IName, Reference> entry : refMap.entrySet()) {
            sb.append(String.format("\n\t\t\t%s = [%s]", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
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
