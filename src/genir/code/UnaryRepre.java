package genir.code;

public class UnaryRepre extends WrittenRepresent{
    public UnaryOp OP;
    public AddressOrData source;

    public UnaryRepre(UnaryOp OP, AddressOrData source, int targetAddr) {
        this.OP = OP;
        this.source = source;
        this.target = new AddressOrData(false, targetAddr);
    }

    @Override
    public String toString() {
        return lineNumToString()+String.format("%-7s %-4s %-4s",OP.toString(),source.toString(),target.toString());
    }

    public static enum UnaryOp{
        MINUS,
        ADD,
        NOT
    }
}
