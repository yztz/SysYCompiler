package ir.code;

import ast.IAstValue;
import common.OP;

public class UnaryIR extends IR {


    public UnaryIR(OP op, IAstValue operand) {
        super(op);
        this.op1 = operand;
    }

}
