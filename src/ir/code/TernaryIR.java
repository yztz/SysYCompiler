package ir.code;

import ast.IAstValue;
import ast.Immediate;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TernaryIR extends IR {
    public TernaryIR(OP op, IAstValue rd, IAstValue rn, IAstValue rm) {
        super(op);
        this.op1 = rd;
        if (op2 instanceof Immediate) {
            this.op3 = rn;
            this.op2 = rm;
        } else {
            this.op2 = rn;
            this.op3 = rm;
        }
    }


    @Override
    public IName getLVal() {
        return ((IName) op1);
    }
}
