package ir.code;

import ast.AstValue;
import ast.OP;

public class UnaryIR extends IR {
    public OP op;
    public AstValue operand;

    public UnaryIR(OP op, AstValue operand) {
        this.op = op;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-7s%-5s", getLabelName(), op, operand == null ? "" : operand);
    }
}
