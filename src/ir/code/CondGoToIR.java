package ir.code;

import ast.IAstValue;
import ast.Immediate;
import common.ILabel;
import common.OP;
import asm.IName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CondGoToIR extends IR{

    public CondGoToIR(ILabel target, IAstValue ident) {
        super(OP.COND_GOTO);
        this.op1 = target;
        this.op2 = ident;
    }


    @Override
    public IName getLVal() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-4s\tif %s != 0 goto %-5s", getLabelName(), op2, op1);
    }
}
