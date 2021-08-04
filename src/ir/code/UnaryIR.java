package ir.code;

import ast.IAstValue;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public Set<IName> getRVal() {
        Set<IName> list = new HashSet<>();
        list.add(((IName) op1));
        return list;
    }
}
