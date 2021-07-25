package compiler.genir.code;

import compiler.asm.Address;

import java.util.Collection;

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
    public Collection<Address> getAllAddress() {
        Collection<Address> allAddress = super.getAllAddress();
        if(!sourceFirst.isData)
            allAddress.add(new Address(sourceFirst));

        if(!sourceSecond.isData)
            allAddress.add(new Address(sourceSecond));

        return allAddress;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %-4s %-4s",lineNumToString(),OP.toString(),sourceFirst.toString(),
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
