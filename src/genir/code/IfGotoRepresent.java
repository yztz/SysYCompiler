package genir.code;

public class IfGotoRepresent extends GotoRepresent {

    public IfGotoRepresent(InterRepresent target, RelOp relOp, AddressOrData left, AddressOrData right) {
        super(target);
        this.relOp = relOp;
        this.left = left;
        this.right = right;
    }

    public RelOp relOp;
    public AddressOrData left;
    public AddressOrData right;

    @Override
    public String toString() {
        InterRepresent targetIR = targetHolder.getInterRepresent();
        return lineNumToString()+String.format("if %s %s %s goto %-7d", left, relOp, right,targetIR==null?-1:targetIR.lineNum );
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
