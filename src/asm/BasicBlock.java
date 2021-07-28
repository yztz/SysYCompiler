package asm;

import common.ILabel;
import common.OP;
import ir.code.CondGoToIR;
import ir.code.GoToIR;
import ir.code.IR;

import java.util.*;

public class BasicBlock{
    private static final Map<ILabel, BasicBlock> labelMap = new HashMap<>();
    private static int nextID = 0;


    private List<IR> irs = new ArrayList<>();
    public final int id;

    public Map<IName, Reference> refTable;
    public List<BasicBlock> nodes = new ArrayList<>();


    private BasicBlock(int id) {
        this.id = id;
    }

    public List<IR> getIRs() {
        return Collections.unmodifiableList(irs);
    }

    public void addIR(IR ir) {
        if (ir.label != null) labelMap.put(ir.label, this);
        this.irs.add(ir);
    }

    public static void buildEdges(List<BasicBlock> blocks) {
        BasicBlock prevBlock = null;

        for (BasicBlock block : blocks) {
            if (null != prevBlock) prevBlock.nodes.add(block);

            for (IR ir : block.irs) {
                BasicBlock targetBlock;
                if (ir.op == OP.GOTO) {
                    targetBlock = labelMap.get(((GoToIR)ir).target);
                    block.nodes.add(targetBlock);
                } else if (ir.op == OP.CALL){
                    targetBlock = labelMap.get((ILabel) ir.op2);
                    block.nodes.add(targetBlock);
                }
            }
            IR lastIR = block.irs.get(block.irs.size() - 1);
            if (lastIR.op != OP.GOTO || lastIR instanceof CondGoToIR)
                prevBlock = block;
            else
                prevBlock = null;
        }
    }

    public static BasicBlock newBlock() {
        BasicBlock ret = new BasicBlock(nextID++);

        return ret;
    }

    @Override
    public String toString() {
        return id + "";
    }

    public void printBlock() {
        System.out.println("========" + id + "========");
        for (IR ir : irs) {
            System.out.print(ir);
            System.out.println(ir.ref2String());
        }
        System.out.printf("Target Blocks: %s\n", nodes.toString());
    }

}
