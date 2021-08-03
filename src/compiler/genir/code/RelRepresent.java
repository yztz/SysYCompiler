package compiler.genir.code;

import compiler.asm.AddressRWInfo;

import java.util.Collection;

public class RelRepresent extends WrittenRepresent{
    public IfGotoRepresent.RelOp op;
    public AddressOrData left;
    public AddressOrData right;

    public RelRepresent(int targetAddress, IfGotoRepresent.RelOp op, AddressOrData left, AddressOrData right) {
        super(targetAddress);
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s: rel %s %s %s %s",lineNumToString(),left,op,right,target);
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        Collection<AddressRWInfo> addressRWInfo = super.getAllAddressRWInfo();
        if(!left.isData)
            addressRWInfo.add(new AddressRWInfo(left));

        if(!right.isData)
            addressRWInfo.add(new AddressRWInfo(right));

        return addressRWInfo;
    }
}
