package genir.code;

public class InterRepresent {
    public int lineNum;

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
