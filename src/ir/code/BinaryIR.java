package ir.code;


import ast.AstValue;
import ast.OP;

public class BinaryIR extends IR {
    public OP op;
    public AstValue rd;
    public AstValue rn;

    public BinaryIR(OP op, AstValue rd, AstValue rn) {
        this.op = op;
        this.rd = rd;
        this.rn = rn;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-10s%-5s%-5s", getLabelName(),op, rd, rn);
    }
}
