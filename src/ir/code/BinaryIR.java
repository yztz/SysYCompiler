package ir.code;


import ast.IAstValue;
import common.OP;
import asm.IName;

import java.util.function.Consumer;

public class BinaryIR extends IR {

    public BinaryIR(OP op, IAstValue rd, IAstValue rn) {
        super(op);
        this.op1 = rd;
        this.op2 = rn;
    }

    @Override
    public void traverseLVal(Consumer<IName> handler) {
        if (op1 instanceof IName) handler.accept((IName)op1);
    }

    @Override
    public void traverseRVal(Consumer<IName> handler) {
        if (op2 instanceof IName) handler.accept((IName) op2);
    }
}
