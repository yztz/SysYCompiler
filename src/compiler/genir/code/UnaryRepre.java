package compiler.genir.code;

import compiler.asm.AddressRWInfo;

import java.util.Collection;

public class UnaryRepre extends WrittenRepresent{
    public UnaryOp OP;
    public AddressOrData source;

    public UnaryRepre(UnaryOp OP, AddressOrData source, int targetAddr) {
        this.OP = OP;
        this.source = source;
        this.target = new AddressOrData(false, targetAddr);
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        Collection<AddressRWInfo> allAddressRWInfos = super.getAllAddressRWInfo();
        if(!source.isData)
            allAddressRWInfos.add(new AddressRWInfo(source));

        return allAddressRWInfos;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %-4s",lineNumToString(),OP.toString(),source.toString(),target.toString());
    }

    public static enum UnaryOp{
        MINUS,
        ADD,
        NOT
    }
}
