package genir.code;

import asm.Address;
import symboltable.ValueSymbol;

import java.util.Collection;

public class LoadRepresent extends WrittenRepresent{
    public ValueSymbol valueSymbol;
    public AddressOrData offset;

    public LoadRepresent(ValueSymbol valueSymbol, AddressOrData offset, int targetAddress) {
        super(targetAddress);
        this.valueSymbol = valueSymbol;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %s[%s] %-7s", lineNumToString(), "LOAD", valueSymbol.symbolToken.getText(),
                             offset.toString(), target.toString());
    }

    @Override
    public Collection<Address> getAllAddress() {
        Collection<Address> allAddress = super.getAllAddress();
        if(!offset.isData)
            allAddress.add(new Address(offset));

        return allAddress;
    }
}
