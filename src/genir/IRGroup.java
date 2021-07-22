package genir;

import com.sun.istack.internal.Nullable;
import genir.code.InterRepresent;
import genir.code.InterRepresentHolder;

import java.util.LinkedList;

public class IRGroup extends AbstractIR{
    String description;

    public IRGroup(String description) {
        this.description = description;
    }

    int id = 0;
    public final LinkedList<IRSection> irSections = new LinkedList<>();

    int nextSectionID = 0;
    public void addSection(IRSection irSection)
    {
        if(irSections.size()>0)
        {
            irSection.setId(irSections.getLast().getLineOccupied());
        }
        irSection.group = this;
        irSection.setId(nextSectionID++);
        irSections.add(irSection);
        if(irSection.getSize()>0)
        {
            fullFillVacancy(irSection.getFirst());
        }else{
            for (InterRepresentHolder holder : vacancyHolders) {
                irSection.bookVacancy(holder);
            }
        }
    }
    public int getSectionSize()
    {
        return irSections.size();
    }


    @Override
    public int getLineOccupied() {
        int total = 0;
        for (IRSection irSection : irSections) {
            total += irSection.getLineOccupied();
        }
        return total;
    }

    @Nullable
    public InterRepresent getFirstIR()
    {
        if(irSections.size()==0)
            return null;
        return irSections.getFirst().getFirst();
    }
    @Nullable
    public InterRepresent getLastIR()
    {
        if(irSections.size()==0)
            return null;
        return irSections.getLast().getLast();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(description).append("\r\n");
        for (IRSection section : irSections) {
            sb.append(section.toString());
        }
        return sb.toString();
    }
}
