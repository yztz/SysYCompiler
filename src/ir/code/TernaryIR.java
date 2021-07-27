package ir.code;

import ast.IAstValue;
import common.OP;
import common.symbol.Variable;
import ir.IName;

import java.util.function.Consumer;

public class TernaryIR extends IR{
    public TernaryIR(OP op, IAstValue rd, IAstValue rn, IAstValue rm) {
        super(op);
        this.op1 = rd;
        this.op2 = rn;
        this.op3 = rm;
    }

    @Override
    public void traverseLVal(Consumer<IName> handler) {
        if (op1 instanceof IName) handler.accept((IName) op1);
    }

    @Override
    public void traverseRVal(Consumer<IName> handler) {
        if (op2 instanceof IName) handler.accept((IName) op2);
        if (op3 instanceof IName) handler.accept((IName) op3);
    }
}
