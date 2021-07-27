package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.asm.Reference;

import compiler.asm.Reg;
import compiler.genir.IRSection;
import compiler.genir.IRGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class InterRepresent{
    public IRGroup section;
    public IRSection getSection()
    {
        return section.getSection();
    }
    public IRGroup getGroup()
    {
        return section;
    }

    public Map<AddressRWInfo, Reference> refMap = new HashMap<>();

    
    private String label;

    public boolean hasLabel()
    {
        return label!=null;
    }
    
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int lineNum;

    public int getLineNum() {
        return lineNum;
    }


    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineOccupied() {
        return 1;
    }

    public String lineNumToString()
    {
        return getPath()/* String.format("%-6d: ",lineNum)*/;
    }

    public String getPath()
    {
        return String.format("%03d-%03d-%03d",
                             getSection()==null?999:
                             getSection().getID(),
                             getGroup()==null?999:
                             getGroup().getID(), lineNum);
    }

    public abstract Collection<AddressRWInfo> getAllAddressRWInfo();

    public Reg addressMapRule(AddressOrData address)
    {
        return null;
    }
}
