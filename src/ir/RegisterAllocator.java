package ir;

import common.ILabel;
import common.OP;
import ir.code.GoToIR;
import ir.code.IR;
import ir.code.UnaryIR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterAllocator {

    public void transBlock(IRs irs) {
        Set<IR> enterPoints = new HashSet<>();   // 入口点
        enterPoints.add(irs.getIR(0));
        for (int i = 1; i < irs.size(); i++) {
            IR ir = irs.getIR(i);
            if (ir.op == OP.GOTO) {
                enterPoints.add(irs.getIR(i + 1));
                IR target = irs.getIR(((GoToIR)ir).target);
                enterPoints.add(target);
            } else if (ir.op == OP.CALL){
                enterPoints.add(irs.getIR(i + 1));

            }
        }
    }
}
