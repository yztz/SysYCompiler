package genir;

import asm.AsmBuilder;
import genir.code.InterRepresent;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.Collection;
import java.util.LinkedList;

public class IRUnion {
    private final LinkedList<AbstractIR> children = new LinkedList<>();

    public void addIR(AbstractIR abstractIR)
    {
        children.add(abstractIR);
    }
    public Collection<AbstractIR> getAll()
    {
        return children;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractIR child : children) {
            sb.append(child.toString()).append("\r\n");
        }
        return sb.toString();
    }
}
