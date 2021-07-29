package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;

public class LAddrRepresent extends WrittenRepresent{
    public ValueSymbol valueSymbol;
    public AddressOrData offset;
    public LAddrRepresent(int targetAddress, ValueSymbol valueSymbol) {
        super(targetAddress);
        this.valueSymbol = valueSymbol;
        offset = new AddressOrData(true,0);
    }

    public LAddrRepresent(AddressOrData target, ValueSymbol valueSymbol) {
        super(target);
        this.valueSymbol = valueSymbol;
        offset = new AddressOrData(true,0);
    }

    public LAddrRepresent(AddressOrData target, ValueSymbol valueSymbol, AddressOrData offset) {
        super(target);
        this.valueSymbol = valueSymbol;
        this.offset = offset;
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        Collection<AddressRWInfo> allAddressRWInfo = super.getAllAddressRWInfo();
        if(!offset.isData)
            allAddressRWInfo.add(new AddressRWInfo(offset,false));
        return allAddressRWInfo;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %s+%s",lineNumToString(),
                             "LADDR",target.toString(),valueSymbol.symbolToken.getText(),
                             offset.toString());
    }
}
