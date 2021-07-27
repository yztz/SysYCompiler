package ir;

import ir.code.IR;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private static int nextID = 0;
    private List<IR> irs;
    public final int id;

    private BasicBlock(int id) {
        this.id = id;
    }

    public void addIR(IR ir) {
        this.irs.add(ir);
    }

    public static BasicBlock newBlock() {
        BasicBlock ret = new BasicBlock(nextID++);

        return ret;
    }


}
