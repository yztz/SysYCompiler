package genir;

import genir.code.InterRepresent;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.Collection;
import java.util.LinkedList;

public class IRUnion {
    public LinkedList<AbstractIR> children = new LinkedList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractIR child : children) {
            sb.append(child.toString()).append("\r\n");
        }
        return sb.toString();
    }
}
