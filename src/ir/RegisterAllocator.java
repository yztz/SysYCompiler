package ir;

import common.ILabel;
import common.OP;
import ir.code.GoToIR;
import ir.code.IR;

import java.util.*;

public class RegisterAllocator {
    public List<BasicBlock> blocks = new ArrayList<>();

    public RegisterAllocator(IRs irs) {
        transBlock(irs);
        calcNextRef();
    }

    public void transBlock(IRs irs) {
        int len = irs.size();
        Set<IR> enterPoints = new HashSet<>();   // 入口点
        enterPoints.add(irs.getIR(0));
        for (int i = 1; i < len; i++) {
            IR ir = irs.getIR(i);
            if (ir.op == OP.GOTO) {
                if(i < len - 1) enterPoints.add(irs.getIR(i + 1));
                IR target = irs.getIR(((GoToIR)ir).target);
                enterPoints.add(target);
            } else if (ir.op == OP.CALL){
                if(i < len - 1) enterPoints.add(irs.getIR(i + 1));
                IR target = irs.getIR((ILabel) ir.op2);
                enterPoints.add(target);
            } else if (ir.op == OP.RETURN) {
                if(i < len - 1) enterPoints.add(irs.getIR(i + 1));  // 返回语句是call的下一条语句
            }
        }
        for (int i = 0; i < len;) {
            IR ir = irs.getIR(i);
            if (enterPoints.contains(ir)) { //是入口点
                BasicBlock block = BasicBlock.newBlock();
                block.addIR(ir);
                i++;
                for (int j = i; j < len; j++,i++) {
                    ir = irs.getIR(j);
                    if (!enterPoints.contains(ir)) {    // 不是入口点
                        block.addIR(ir);
                    } else {
                        break;
                    }
                }
                blocks.add(block);
            }
        }
        BasicBlock.buildEdges(blocks);
    }
    public void calcNextRef() {
        for (BasicBlock block : blocks) {
            Map<IName, Reference> refMap = new HashMap<>();
            List<IR> irs = block.getIRs();
            for (int i = irs.size() - 1; i >= 0; i--) {
                // todo pass：可以删去x不活跃的语句
                IR ir = irs.get(i);
                ir.traverseLVal(name -> {
                    refMap.putIfAbsent(name, new Reference(null, true));
                    ir.refMap.put(name, refMap.get(name));
                    refMap.put(name, new Reference(null, false));
                });
                ir.traverseRVal(name -> {
                    refMap.putIfAbsent(name, new Reference(null, true));
                    ir.refMap.put(name, refMap.get(name));
                    refMap.put(name, new Reference(ir, true));
                });
            }
        }
    }
}
