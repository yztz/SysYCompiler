package ir.code;


import ast.IAstValue;
import ast.OP;

public class BinaryIR extends IR {
    public OP op;
    public IAstValue rd;
    public IAstValue rn;

    public BinaryIR(OP op, IAstValue rd, IAstValue rn) {
        this.op = op;
        this.rd = rd;
        this.rn = rn;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-10s%-5s%-5s", getLabelName(),op, rd, rn);
    }
}
