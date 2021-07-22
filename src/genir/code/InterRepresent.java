package genir.code;

import genir.IRGroup;
import genir.IRSection;

public class InterRepresent{
    public IRSection section;
    public IRGroup getGroup()
    {
        return section.getGroup();
    }
    public IRSection getSection()
    {
        return section;
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
}
