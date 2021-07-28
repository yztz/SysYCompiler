package ir;

import common.ILabel;
import common.symbol.Function;
import ir.code.IR;

import java.util.*;
import java.util.function.Consumer;

public class IRs implements Iterable<IR> {
    private List<IR> irs = new ArrayList<>();
    private Map<ILabel, IR> labelMap = new HashMap<>();
    private ILabel nextLabel;
//    private Map<Function, List<IR>>

    private Function currentFunc;

    public int size() {
        return irs.size();
    }

    public void addIR(IR ir) {
        if (null != currentFunc) {

        }
        attachLabel(ir);
        irs.add(ir);
    }

    public void startSection(Function function) {
        currentFunc = function;
    }

    public void endSection() {
        currentFunc = null;
    }

    private void attachLabel(IR ir) {
        if (nextLabel != null) {
            ir.setLabel(nextLabel);
            labelMap.put(nextLabel, ir);
            nextLabel = null;
        }
    }

    public void setNextLabel(ILabel label) {
        this.nextLabel = label;
    }

    public IR getIR(int i) {
        return irs.get(i);
    }

    public IR getIR(ILabel label) {
        return labelMap.get(label);
    }


    @Override
    public Iterator<IR> iterator() {
        return irs.iterator();
    }

    @Override
    public void forEach(Consumer<? super IR> action) {
        irs.forEach(action);
    }

    @Override
    public Spliterator<IR> spliterator() {
        return irs.spliterator();
    }
}
