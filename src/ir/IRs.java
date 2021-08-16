package ir;

import asm.BasicBlock;
import common.ILabel;
import common.symbol.Function;
import ir.code.IR;

import java.util.*;
import java.util.function.Consumer;

public class IRs {
    private static List<IR> irs = new ArrayList<>();
    private static Map<ILabel, IR> labelMap = new HashMap<>();
    private static ILabel nextLabel;
    private static List<Function> functions = new ArrayList<>();
    public static List<BasicBlock> blocks = new ArrayList<>();

    private static Function currentFunc;

    public static List<IR> getIRs() {
        return Collections.unmodifiableList(irs);
    }

    public static IR getIR(ILabel label) {
        return labelMap.get(label);
    }

    public static List<Function> getFunctions() {
        return functions;
    }

    public static void addIR(IR ir) {
        if (null != currentFunc) {
            currentFunc.irs.add(ir);
        }
        attachLabel(ir);
        irs.add(ir);
    }

    public static void startSection(Function function) {
        functions.add(function);
        currentFunc = function;
    }

    public static void endSection() {
        if (currentFunc != null) {
            // 生成基本块
            currentFunc.blocks = BasicBlock.genBlocks(currentFunc);
//            for (BasicBlock block : currentFunc.blocks) block.printBlock();
            blocks.addAll(currentFunc.blocks);
            currentFunc = null;
        }
    }

    private static void attachLabel(IR ir) {
        if (nextLabel != null) {
            ir.setLabel(nextLabel);
            labelMap.put(nextLabel, ir);
            nextLabel = null;
        }
    }

    public static void setNextLabel(ILabel label) {
        if (null != nextLabel) System.err.printf("label[%s] covered by label[%s]%n", nextLabel, label);
        nextLabel = label;
    }

    public static void removeNextLabel() {
//        if (nextLabel != null) System.err.printf("label[%s] removed%n", nextLabel);
        nextLabel = null;
    }

}
