package compiler.optim.refgraph;

import compiler.symboltable.ValueSymbol;

import java.util.Objects;

public class VarRefNode extends RefNode{
    ValueSymbol valueSymbol;

    public VarRefNode(ValueSymbol valueSymbol) {
        this.valueSymbol = valueSymbol;
    }

    @Override
    public String toString() {
        return "VarRefNode{" + "refs=" + refs + ", valueSymbol=" + valueSymbol + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarRefNode that = (VarRefNode) o;
        return valueSymbol.equals(that.valueSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueSymbol);
    }
}
