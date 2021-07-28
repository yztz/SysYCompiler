package ir.code;

import ast.IAstValue;
import common.OP;
import asm.IName;

import java.util.function.Consumer;

public class UnaryIR extends IR {


    public UnaryIR(OP op, IAstValue operand) {
        super(op);
        this.op1 = operand;
    }

    @Override
    public void traverseLVal(Consumer<IName> handler) {

    }

    @Override
    public void traverseRVal(Consumer<IName> handler) {
        if (op1 instanceof IName) handler.accept((IName) op1);
    }
}
