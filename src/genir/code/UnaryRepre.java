package genir.code;

public class UnaryRepre extends InterRepresent{
    public UnaryOp OP;
    public AddressOrNum source;
    public AddressOrNum target;

    public UnaryRepre(UnaryOp OP, AddressOrNum source, int targetAddr) {
        this.OP = OP;
        this.source = source;
        this.target = new AddressOrNum(false,targetAddr);
    }

    @Override
    public String toString() {
        return String.format("%-6d: %-7s %-4s %-4s",lineNum,OP.toString(),source.toString(),target.toString());
    }

    public static enum UnaryOp{
        MINUS,
        ADD,
        NOT
    }
}
