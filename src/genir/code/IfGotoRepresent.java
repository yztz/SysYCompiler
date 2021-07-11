package genir.code;

public class IfGotoRepresent extends GotoRepresent {

    public IfGotoRepresent(InterRepresent target, RelOp relOp, AddressOrNum left, AddressOrNum right) {
        super(target);
        this.relOp = relOp;
        this.left = left;
        this.right = right;
    }

    public RelOp relOp;
    public AddressOrNum left;
    public AddressOrNum right;

    @Override
    public String toString() {
        InterRepresent targetIR = targetHolder.getInterRepresent();
        return String.format("%-6d: if %s %s %s goto %-7d", lineNum, left, relOp, right,targetIR==null?-1:targetIR.lineNum );
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
