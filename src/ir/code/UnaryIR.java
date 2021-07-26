package ir.code;

import ast.IAstValue;
import ast.OP;

public class UnaryIR extends IR {
    public OP op;
    public IAstValue operand;

    public UnaryIR(OP op, IAstValue operand) {
        this.op = op;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-7s%-5s", getLabelName(), op, operand == null ? "" : operand);
    }
}
