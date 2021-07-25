package compiler.genir;

import com.sun.istack.internal.Nullable;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.InterRepresentHolder;

import java.util.LinkedList;

public class IRSection extends AbstractIR{
    String description;

    public IRSection(String description) {
        this.description = description;
    }

    int id = 0;
    private LinkedList<IRGroup> irGroups = new LinkedList<>();

    int nextSectionID = 0;
    public void addGroup(IRGroup irGroup)
    {
        if(irGroups.size()>0)
        {
            irGroup.setId(irGroups.getLast().getLineOccupied());
        }
        irGroup.section = this;
        irGroup.setId(nextSectionID++);
        irGroups.add(irGroup);
        if(irGroup.getSize()>0)
        {
            fullFillVacancy(irGroup.getFirst());
        }else{
            for (InterRepresentHolder holder : vacancyHolders) {
                irGroup.bookVacancy(holder);
            }
        }
    }

    public void insertAfter(IRGroup irGroup,IRGroup index)
    {
        int i = irGroups.indexOf(index) + 1;
        insertInternal(irGroup, i);
    }

    public void insertBefore(IRGroup irGroup,IRGroup index)
    {
        int i = irGroups.indexOf(index);
        insertInternal(irGroup, i);
    }

    private void insertInternal(IRGroup irGroup, int i) {
        irGroup.setId(i);
        irGroup.section = this;
        irGroups.add(i,irGroup);
        for (int j = i+1; j < irGroups.size(); j++) {
            irGroups.get(j).setId(irGroups.get(j).getID()+1);
        }
    }

    public IRSection[] split(IRGroup index,String desc1,String desc2)
    {
        IRSection[] irSections = new IRSection[2];
        int i = irGroups.indexOf(index);
        irSections[0]= new IRSection(desc1);
        irSections[0].irGroups = (LinkedList<IRGroup>) irGroups.subList(0, i-1);
        for (IRGroup group : irSections[0].irGroups) {
            group.section = irSections[0];
        }
        irSections[1]= new IRSection(desc2);
        irSections[1].irGroups = (LinkedList<IRGroup>) irGroups.subList(i, irGroups.size()-1);
        for (IRGroup group : irSections[1].irGroups) {
            group.section = irSections[1];
        }
        return irSections;
    }

    public void merge(IRSection another)
    {
        if(another==null)
            return;
        irGroups.addAll(another.irGroups);
    }
    public int getGroupSize()
    {
        return irGroups.size();
    }


    @Override
    public int getLineOccupied() {
        int total = 0;
        for (IRGroup irGroup : irGroups) {
            total += irGroup.getLineOccupied();
        }
        return total;
    }

    @Override
    public LinkedList<InterRepresent> flatIR() {
        LinkedList<InterRepresent> result = new LinkedList<>();
        for (IRGroup irGroup : irGroups) {
            result.addAll(irGroup.flatIR());
        }
        return result;
    }

    public IRGroup getFirst()
    {
        if(irGroups.size()==0)
            return null;
        return irGroups.getFirst();
    }
    public IRGroup getLast()
    {
        if(irGroups.size()==0)
            return null;
        return irGroups.getLast();
    }

    public IRGroup get(int index)
    {
        return irGroups.get(index);
    }

    @Nullable
    public InterRepresent getFirstIR()
    {
        if(irGroups.size()==0)
            return null;
        return irGroups.getFirst().getFirst();
    }
    @Nullable
    public InterRepresent getLastIR()
    {
        if(irGroups.size()==0 || irGroups.getLast().getSize()==0)
            return null;
        return irGroups.getLast().getLast();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(description).append("\r\n");
        for (IRGroup section : irGroups) {
            sb.append(section.toString());
        }
        return sb.toString();
    }
}
