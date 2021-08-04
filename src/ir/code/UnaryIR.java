package ir.code;

import ast.IAstValue;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UnaryIR extends IR {

    public UnaryIR(OP op, IAstValue operand) {
        super(op);
        this.op1 = operand;
    }


    @Override
    public IName getLVal() {
        return null;
    }

    @Override
    public List<IName> getRVal() {
        List<IName> list = new ArrayList<>();
        list.add(((IName) op1));
        return list;
    }
}
