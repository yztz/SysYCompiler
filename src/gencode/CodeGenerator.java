package gencode;

import genir.IRCode;
import genir.code.BinocularRepre;
import genir.code.GotoRepresent;
import genir.code.InterRepresent;
import symboltable.SymbolTable;
import symboltable.SymbolTableHost;

import java.util.*;

public class CodeGenerator {
    /* 临时变量与寄存器的映射 */
    private Map<Integer, Integer> regMapper = new HashMap<>();
    /* 基本块 */
    private Map<Integer, Block> blockMap = new HashMap<>();
    /* 变量活跃与下次引用 */
    private Map<String, Boolean> aliveTable = new HashMap<>();
    private Map<String, IRCode> RefTable = new HashMap<>();

    private IRCode irCode;
    private SymbolTableHost tableHost;

    public CodeGenerator(IRCode irCode, SymbolTableHost tableHost) {
        this.irCode = irCode;
        this.tableHost = tableHost;
    }

    public void getNextRef() {
        for (Block block : blockMap.values()) {
            for (int i = block.irs.size() - 1; i >= 0; i++) {
                InterRepresent ir = block.irs.get(i);
                if (ir instanceof BinocularRepre) {
                    ir = (BinocularRepre) ir;

                }
            }
        }
    }

    /**
     * 将中间代码划分为块
     */
    public void transBlock() {
        List<InterRepresent> codes = irCode.codes;
        Set<InterRepresent> enterPoints = new HashSet<>();
        int len = codes.size();
        // 第一条语句
        enterPoints.add(codes.get(0));
        for (int i = 0; i < len; i++) {
            InterRepresent ir = codes.get(i);
            if (ir instanceof GotoRepresent) {
                // goto语句的下一条语句
                enterPoints.add(codes.get(i + 1));
                // 目标语句
                enterPoints.add(codes.get(((GotoRepresent) ir).getTargetIR().lineNum));
            }
        }

        for (int i = 0; i < len; i++) {
            InterRepresent ir = codes.get(i);
            if (enterPoints.contains(ir)) {
                Block block = new Block(ir);
                while (++i < len && !enterPoints.contains(codes.get(i))) {
                    // TODO 还未考虑halt
                    block.addIR(codes.get(i));
                }
                blockMap.put(block.id, block);
            }
        }
    }

    static class Block {
        private static int next_id = 0;

        public final int id;
        private List<InterRepresent> irs = new ArrayList<>();


        private Block(InterRepresent startIR) {
            this.id = next_id++;
            this.addIR(startIR);
        }

        public void addIR(InterRepresent ir) {
            if (null == ir) return;
            this.irs.add(ir);
        }

        public int getStartLine() {
            if (irs.size() == 0) return -1;
            return irs.get(0).lineNum;
        }

        public int getEndLine() {
            if (irs.size() == 0) return -1;
            return irs.get(irs.size() - 1).lineNum;
        }
    }

}
