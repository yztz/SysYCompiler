package ast;

import common.symbol.Variable;

public class OffsetVar implements AstValue {
    public Variable variable;
    public AstNode offset;

    public OffsetVar(Variable array, AstNode offset) {
        this.variable = array;
        this.offset = offset;
    }

    public OffsetVar(Variable array, int offset) {
        this.variable = array;
        this.offset = AstNode.makeLeaf(offset);
    }

    @Override
    public String toString() {
        return getVal();
    }

    @Override
    public String getVal() {
        if (null == offset.value)
            return variable.name + "[exp]";
        else
            return variable.name + '[' + offset.value + ']';
    }
}
