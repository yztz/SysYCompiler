package genir.code;

public class BinocularRepre extends InterRepresent{
    public BinocularRepre(Opcodes OP, AddressOrNum sourceFirst, AddressOrNum sourceSecond,
                          int targetAddr) {
        this.OP = OP;
        this.sourceFirst = sourceFirst;
        this.sourceSecond = sourceSecond;
        this.target = new AddressOrNum(false,targetAddr);
    }

    public Opcodes OP;

    public AddressOrNum sourceFirst;
    public AddressOrNum sourceSecond;
    public AddressOrNum target;

    @Override
    public String toString() {
        return String.format("%-6d: %-7s %-4s %-4s %-4s",lineNum,OP.toString(),sourceFirst.toString(),
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
