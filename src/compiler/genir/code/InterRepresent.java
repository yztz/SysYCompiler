package compiler.genir.code;

import compiler.Location;
import compiler.asm.AddressRWInfo;
import compiler.asm.Reference;

import compiler.asm.Reg;
import compiler.genir.IRCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class InterRepresent{
    public IRCollection collection;
    public Location location = Location.defaultLoc;
    public boolean startOfBlock;
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
        return String.format("%03d-%03d",
                             collection==null?999:
                                     collection.getID(), lineNum);
    }

    public abstract Collection<AddressRWInfo> getAllAddressRWInfo();

    public Reg addressMapRule(AddressOrData address)
    {
        return null;
    }

    public abstract InterRepresent createCopy();
}
