package ir.code;


import ast.IAstValue;
import ast.Immediate;
import common.OffsetVar;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BinaryIR extends IR {

    public BinaryIR(OP op, IAstValue rd, IAstValue rn) {
        super(op);
        this.op1 = rd;
        this.op2 = rn;
//        if (rd instanceof Immediate) {
//            this.op1 = rn;
//            this.op2 = rd;
//        } else {
//            this.op1 = rd;
//            this.op2 = rn;
//        }

    }

    @Override
    public Set<IName> getRVal() {
        Set<IName> rVal = super.getRVal();
        if (op1 instanceof OffsetVar) rVal.addAll(((OffsetVar) op1).parseSelf());
        Set<IName> tmpSet = new HashSet<>();
        for (IName name : rVal) {
            if (name instanceof OffsetVar) tmpSet.addAll(((OffsetVar) name).parseSelf());
        }
        rVal.addAll(tmpSet);
        return rVal;
    }

    @Override
    public IName getLVal() {
        return ((IName) op1);
    }
}
