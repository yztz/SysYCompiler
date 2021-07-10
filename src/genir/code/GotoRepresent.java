package genir.code;

public class GotoRepresent extends InterRepresent{
    public int targetLineNum;

    public GotoRepresent(int targetLineNum) {
        this.targetLineNum = targetLineNum;
    }

    @Override
    public String toString() {
        return String.format("%-6d: goto %-7d",lineNum,targetLineNum);
    }
}
