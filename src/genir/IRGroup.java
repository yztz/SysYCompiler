package genir;

import genir.code.InterRepresent;

import java.util.LinkedList;

/**
 * 一个section对应一行SysY代码
 * 一个section可能有多个IR代码
 */
public class IRGroup extends AbstractIR{
    public String description;
    private int nextLineNum = 0;
    private final LinkedList<InterRepresent> _irs =new LinkedList<>();
    public IRSection section;
    public IRSection getSection()
    {
        return section;
    }
    public IRGroup(String description) {
        this.description = description;
    }

    public InterRepresent getFirst()
    {
        return _irs.getFirst();
    }
    public InterRepresent getLast()
    {
        return _irs.getLast();
    }


    public void addCode( InterRepresent ir)
    {
        if(ir==null)
            return;
        ir.section = this;
        ir.setLineNum(nextLineNum);
        nextLineNum++;
        _irs.addLast(ir);
        fullFillVacancy(ir);
    }
    public void insertCode(int index,InterRepresent code)
    {
        _irs.stream().parallel().forEach(t->{
            if (t.getLineNum()>=index)
                t.setLineNum(t.getLineNum()+1);
        });
        code.setLineNum(index);
        _irs.add(index,code);
        nextLineNum++;
        //fullFillQuad(code);
    }

    public void merge(IRGroup irGroup)
    {
        for (InterRepresent ir : irGroup._irs) {
            addCode(ir);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        for (InterRepresent ir : _irs) {

            if(!firstLine)
            {
                sb.append(ir.toString());
            }else{
                sb.append(String.format("%-48s @%s",ir.toString(),description));
                firstLine=false;
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public int getLineOccupied() {
        int totalOccupied = 0;
        for (InterRepresent ir : _irs) {
            totalOccupied+=ir.getLineOccupied();
        }
        return totalOccupied;
    }

    @Override
    public LinkedList<InterRepresent> flatIR() {
        return _irs;
    }

    public int getSize()
    {
        return _irs.size();
    }
}
