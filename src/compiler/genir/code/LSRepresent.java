package compiler.genir.code;

import compiler.asm.AddressRWInfo;
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
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        Collection<AddressRWInfo> allAddressRWInfos = super.getAllAddressRWInfo();
        if(!offset.isData)
            allAddressRWInfos.add(new AddressRWInfo(offset));

        return allAddressRWInfos;
    }
}
