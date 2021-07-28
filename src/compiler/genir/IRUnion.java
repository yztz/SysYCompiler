package compiler.genir;

import java.util.Collection;
import java.util.LinkedList;

public class IRUnion {
    private final LinkedList<IRCollection> children = new LinkedList<>();

    public void addIR(IRCollection IRCollection)
    {
        children.add(IRCollection);
    }
    public Collection<IRCollection> getAll()
    {
        return children;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRCollection child : children) {
            sb.append(child.toString()).append("\r\n");
        }
        return sb.toString();
    }
}
