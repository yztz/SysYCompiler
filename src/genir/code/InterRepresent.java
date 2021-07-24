package genir.code;

import asm.Address;
import asm.Reference;
import com.sun.istack.internal.Nullable;
import genir.IRSection;
import genir.IRGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class InterRepresent{
    public IRGroup section;
    public IRSection getGroup()
    {
        return section.getSection();
    }
    public IRGroup getSection()
    {
        return section;
    }

    public Map<Address, Reference> refMap = new HashMap<>();

    @Nullable
    private String label;

    public boolean hasLabel()
    {
        return label!=null;
    }
    @Nullable
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
        return String.format("%03d-%03d-%03d",getGroup().getID(),getSection().getID(),lineNum);
    }

    public abstract Collection<Address> getAllAddress();
}
