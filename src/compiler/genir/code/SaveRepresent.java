package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.symboltable.ValueSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SaveRepresent extends LSRepresent{


    public SaveRepresent(ValueSymbol valueSymbol, AddressOrData offset, AddressOrData source) {
        super(valueSymbol, offset, source);
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %s[%s]",lineNumToString(),"SAVE",target.toString(),
                             valueSymbol.symbolToken.getText(),
                                               offset.toString());
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        List<AddressRWInfo> allAddressRWInfos = new ArrayList<>();
        allAddressRWInfos.add(new AddressRWInfo(target,false));
        if(!offset.isData)
            allAddressRWInfos.add(new AddressRWInfo(offset));

        return allAddressRWInfos;
    }

    @Override
    public InterRepresent createCopy() {
        return new SaveRepresent(valueSymbol,offset.copy(),target.copy());
    }
}
