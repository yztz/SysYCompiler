package ir.code;

import ast.IAstValue;
import common.ILabel;
import common.OP;
import asm.IName;
import asm.allocator.Reference;
import common.symbol.Function;

import java.util.*;
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

    public abstract IName getLVal();

    public Set<IName> getRVal() {
        Set<IName> list = new HashSet<>();
        if (op2 instanceof IName) list.add(((IName) op2));
        if (op3 instanceof IName) list.add(((IName) op3));
        return list;
    }

    public List<IName> getNames() {
        List<IName> list = new ArrayList<>();
        if (op1 instanceof IName) list.add(((IName) op1));
        if (op2 instanceof IName) list.add(((IName) op2));
        if (op3 instanceof IName) list.add(((IName) op3));

        return list;
    }

//    public abstract IName getLVal();
//    public abstract List<IName> getRVal();
//    public List<IName> getAllName() {
//        List<IName> names = getRVal();
//        IName lVal = getLVal();
//        if (lVal != null)
//            names.add(lVal);
//
//        return names;
//    }


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

    public boolean isJump() {
        return op == OP.GOTO ||
                op == OP.COND_GOTO;
    }

    public boolean isReturn() {
        return op == OP.RETURN;
    }
}
