package ir.code;


import ast.IAstValue;
import common.OP;

public class BinaryIR extends IR {

    public BinaryIR(OP op, IAstValue rd, IAstValue rn) {
        super(op);
        this.op1 = rd;
        this.op2 = rn;
    }

}
