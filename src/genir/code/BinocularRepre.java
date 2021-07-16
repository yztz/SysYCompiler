package genir.code;

public class BinocularRepre extends WrittenRepresent{
    public BinocularRepre(Opcodes OP, AddressOrData sourceFirst, AddressOrData sourceSecond,
                          int targetAddr) {
        this.OP = OP;
        this.sourceFirst = sourceFirst;
        this.sourceSecond = sourceSecond;
        this.target = new AddressOrData(false, targetAddr);
    }

    public Opcodes OP;

    public AddressOrData sourceFirst;
    public AddressOrData sourceSecond;

    @Override
    public String toString() {
        return lineNumToString()+String.format("%-7s %-4s %-4s %-4s",OP.toString(),sourceFirst.toString(),
                             sourceSecond.toString(),
                             target.toString());
    }

    public enum Opcodes{
        ADD,
        MINUS,
        MUL,
        DIV,
        MOD
    }
}
