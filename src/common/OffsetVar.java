package common;

import ast.AstNode;
import ast.IAstValue;
import ast.Utils;
import common.symbol.Variable;
import asm.IName;

import java.util.Objects;

public class OffsetVar implements IName {
    public Variable variable;
    public AstNode offsetTree;
    public boolean isLVal = false;

    public OffsetVar(Variable array, AstNode offsetTree) {
        this.variable = array;
        this.offsetTree = Utils.calc(offsetTree);
    }

    public OffsetVar(Variable array, int offsetTree) {
        this.variable = array;
        this.offsetTree = AstNode.makeLeaf(offsetTree);
    }

    public IAstValue getOffset() {
        return offsetTree.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetVar offsetVar = (OffsetVar) o;
        return Objects.equals(variable, offsetVar.variable) && Objects.equals(offsetTree.value, offsetVar.offsetTree.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, offsetTree.value);
    }

    @Override
    public String toString() {
        if (null == offsetTree.value)
            return variable.name + "[exp]";
        else
            return variable.name + '[' + offsetTree.value + ']';
    }


    @Override
    public Object getVal() {
        return variable;
    }
}
