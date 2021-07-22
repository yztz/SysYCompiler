package genir.code;

import genir.IRSection;
import genir.IRGroup;

public class InterRepresent{
    public IRGroup section;
    public IRSection getGroup()
    {
        return section.getSection();
    }
    public IRGroup getSection()
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
