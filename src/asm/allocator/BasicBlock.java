package asm.allocator;

import asm.IName;
import common.ILabel;
import common.OP;
import common.OffsetVar;
import common.symbol.Variable;
import ir.IRs;
import ir.Temp;
import ir.code.BinaryIR;
import ir.code.CondGoToIR;
import ir.code.GoToIR;
import ir.code.IR;

import java.util.*;

public class BasicBlock {
    public static BasicBlock ENTRY_BLOCK = new BasicBlock(-1);
    public static BasicBlock EXIT_BLOCK = new BasicBlock(-2);

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

    public IR getIR(int i) {
        return irs.get(i);
    }

    public void addIR(IR ir) {
        if (ir.label != null) labelMap.put(ir.label, this);
        this.irs.add(ir);
    }

    public int indexIR(IR ir) {
        return irs.indexOf(ir);
    }

    public static void buildEdges(List<BasicBlock> blocks) {
        BasicBlock prevBlock = null;

        for (BasicBlock block : blocks) {
            if (null != prevBlock) prevBlock.nodes.add(block);

            for (IR ir : block.irs) {
                BasicBlock targetBlock;
                if (ir.isJump()) {
                    targetBlock = labelMap.get(ir.op1);
                    block.nodes.add(targetBlock);
                } else if (ir.op == OP.CALL) {
                    targetBlock = labelMap.get((ILabel) ir.op2);
                    block.nodes.add(targetBlock);
                }
            }
            IR lastIR = block.irs.get(block.irs.size() - 1);
            if (!lastIR.isJump() || lastIR instanceof CondGoToIR)
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

    public static List<BasicBlock> genBlocks(List<IR> irs) {
        List<BasicBlock> blocks = new ArrayList<>();
        int len = irs.size();
        Set<IR> enterPoints = new HashSet<>();   // 入口点
        enterPoints.add(irs.get(0));
        for (int i = 1; i < len; i++) {
            IR ir = irs.get(i);
            if (ir.isJump()) {
                if (i < len - 1) enterPoints.add(irs.get(i + 1));
                IR target = IRs.getIR((ILabel) ir.op1);
                enterPoints.add(target);
            } else if (ir.op == OP.CALL) {
                enterPoints.add(irs.get(i));
//                if (i < len - 1) enterPoints.add(irs.getIR(i + 1));
//                IR target = IRs.getIR((ILabel) ir.op2);
//                enterPoints.add(target);
            } else if (ir.op == OP.RETURN) {
                if (i < len - 1) enterPoints.add(irs.get(i + 1));  // return返回语句是call的下一条语句
            }
        }
        for (int i = 0; i < len; ) {
            IR ir = irs.get(i);
            if (enterPoints.contains(ir)) { //是入口点
                BasicBlock block = BasicBlock.newBlock();
                block.addIR(ir);
                i++;
                for (int j = i; j < len; j++, i++) {
                    ir = irs.get(j);
                    if (!enterPoints.contains(ir)) {    // 不是入口点
                        block.addIR(ir);
                    } else {
                        break;
                    }
                }
                blocks.add(block);
            }
        }
        calcNextRef(blocks);
        return blocks;
    }

    private static void calcNextRef(List<BasicBlock> blocks) {
        for (BasicBlock block : blocks) {
            Map<IName, Reference> refTable = new HashMap<>();
            List<IR> irs = block.getIRs();
            for (int i = irs.size() - 1; i >= 0; i--) {
                // todo pass：可以删去x不活跃的语句
                IR ir = irs.get(i);
                IName lVal = ir.getLVal();

                List<IName> rVal = ir.getRVal();
                if (lVal != null) {
                    if (lVal instanceof Temp)
                        refTable.putIfAbsent(lVal, new Reference(null, false));
                    else
                        refTable.putIfAbsent(lVal, new Reference(null, true));

                    ir.refMap.put(lVal, refTable.get(lVal));
                    refTable.put(lVal, new Reference(null, false));

                }
                rVal.forEach(name -> {
                    if (name instanceof Temp)
                        refTable.putIfAbsent(name, new Reference(null, false));
                    else
                        refTable.putIfAbsent(name, new Reference(null, true));
                    ir.refMap.put(name, refTable.get(name));
                    refTable.put(name, new Reference(ir, true));
                });
            }
            block.refTable = refTable;
        }
    }


}
