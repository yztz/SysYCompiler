package genir.code;

import asm.Address;
import symboltable.ValueSymbol;
import symboltable.VarSymbol;

import java.util.Collection;

public class SaveRepresent extends WrittenRepresent{
    public ValueSymbol valueSymbol;
    public AddressOrData offset;

    public SaveRepresent(ValueSymbol valueSymbol, AddressOrData offset, AddressOrData source) {
        super(source);
        this.valueSymbol = valueSymbol;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %s[%s]",lineNumToString(),"SAVE",target.toString(),
                             valueSymbol.symbolToken.getText(),
                                               offset.toString());
    }

    @Override
    public Collection<Address> getAllAddress() {
        Collection<Address> allAddress = super.getAllAddress();
        if(!offset.isData)
            allAddress.add(new Address(offset));

        return allAddress;
    }
}
