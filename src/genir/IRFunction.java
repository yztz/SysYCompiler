package genir;

import java.util.LinkedList;

public class IRFunction extends AbstractIR {
    private final LinkedList<IRGroup> _groups = new LinkedList<>();
    int nextGroupID = 0;

    public IRFunction() {
        addGroup(new IRGroup("function body"));
    }

    /**
     * 该组结束，开始新的一组代码
     */
/*    public void endGroup()
    {
        addGroup()
    }*/

    public void addGroup(IRGroup group)
    {
        group.setId(nextGroupID++);
        _groups.add(group);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRGroup section : _groups) {
            sb.append(section.toString()).append("\r\n");
        }
        return sb.toString();
    }

    public IRGroup getLast()
    {
        return _groups.getLast();
    }



    @Override
    public int getLineOccupied() {
        int totalOccupied = 0;
        for (AbstractIR ir : _groups) {
            totalOccupied+=ir.getLineOccupied();
        }
        return totalOccupied;
    }
}
