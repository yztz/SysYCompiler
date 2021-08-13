package compiler.genir.code;

import compiler.asm.AddressRWInfo;

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
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        Collection<AddressRWInfo> allAddressRWInfos = super.getAllAddressRWInfo();
        if(!sourceFirst.isData)
            allAddressRWInfos.add(new AddressRWInfo(sourceFirst));

        if(!sourceSecond.isData)
            allAddressRWInfos.add(new AddressRWInfo(sourceSecond));

        return allAddressRWInfos;
    }

    @Override
    public InterRepresent createCopy() {
        return new BinocularRepre(OP,sourceFirst.copy(),sourceSecond.copy(),target.item);
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
