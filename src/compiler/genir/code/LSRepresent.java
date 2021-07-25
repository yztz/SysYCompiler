package compiler.genir.code;

import compiler.asm.Address;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;

public class LSRepresent extends WrittenRepresent {
    public ValueSymbol valueSymbol;
    public AddressOrData offset;

    public LSRepresent(ValueSymbol valueSymbol, AddressOrData offset, AddressOrData source) {
        super(source);
        this.valueSymbol = valueSymbol;
        this.offset = offset;
    }


    @Override
    public Collection<Address> getAllAddress() {
        Collection<Address> allAddress = super.getAllAddress();
        if(!offset.isData)
            allAddress.add(new Address(offset));

        return allAddress;
    }
}
