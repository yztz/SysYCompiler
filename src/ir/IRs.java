package ir;

import common.ILabel;
import ir.code.IR;

import java.util.*;
import java.util.function.Consumer;

public class IRs implements Iterable<IR> {
    private List<IR> irs = new ArrayList<>();
    private Map<ILabel, IR> labelMap = new HashMap<>();
    private ILabel nextLabel;


    public int size() {
        return irs.size();
    }

    public void addIR(IR ir) {
        if (nextLabel != null) {
            ir.setLabel(nextLabel);
            labelMap.put(nextLabel, ir);
            nextLabel = null;
        }
        irs.add(ir);
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
