package genir.code;

import gencode.Address;
import gencode.Ref;

import java.util.HashMap;
import java.util.Map;

public class InterRepresent {
    public int lineNum;

    public Map<Address, Ref> refMap = new HashMap<>();

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String lineNumToString()
    {
        return String.format("%-6d: ",lineNum);
    }

}
