package genir.code;

public class IfGotoRepresent extends GotoRepresent {

    public IfGotoRepresent(int targetLineNum, RelOp relOp, AddressOrNum left, AddressOrNum right) {
        super(targetLineNum);
        this.relOp = relOp;
        this.left = left;
        this.right = right;
    }

    public RelOp relOp;
    public AddressOrNum left;
    public AddressOrNum right;

    @Override
    public String toString() {
        return String.format("%-6d: if %s %s %s goto %-7d",lineNum,left,relOp,right,targetLineNum);
    }

    public enum RelOp{
        LESS,
        GREATER,
        LESS_EQUAL,
        GREATER_EQUAL,
        EQUAL,
        NOT_EQUAL
    }
}
