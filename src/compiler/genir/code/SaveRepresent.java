package compiler.genir.code;

import compiler.asm.Address;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;

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
}