package ir.code;


import ast.IAstValue;
import ast.Immediate;
import common.OffsetVar;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BinaryIR extends IR {

    public BinaryIR(OP op, IAstValue rd, IAstValue rn) {
        super(op);
        if (rd instanceof Immediate) {
            this.op1 = rn;
            this.op2 = rd;
        } else {
            this.op1 = rd;
            this.op2 = rn;
        }

    }


    @Override
    public IName getLVal() {
        return ((IName) op1);
    }
}
