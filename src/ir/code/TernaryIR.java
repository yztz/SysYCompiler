package ir.code;

import ast.IAstValue;
import common.OP;

public class TernaryIR extends IR{
    public TernaryIR(OP op, IAstValue rd, IAstValue rn, IAstValue rm) {
        super(op);
        this.op1 = rd;
        this.op2 = rn;
        this.op3 = rm;
    }

}
