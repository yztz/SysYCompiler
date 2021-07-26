package ir.code;

import ast.AstValue;
import ast.OP;

public class TernaryIR extends IR{
    public OP op;
    public AstValue rd, rn, rm;

    public TernaryIR(OP op, AstValue rd, AstValue rn, AstValue rm) {
        this.op = op;
        this.rd = rd;
        this.rn = rn;
        this.rm = rm;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-7s%-5s%-5s%-5s", getLabelName(),op, rd, rn, rm);
    }
}
