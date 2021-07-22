package genir.code;

import symboltable.VarSymbol;

public class SaveRepresent extends WrittenRepresent{
    public VarSymbol varSymbol;
    public AddressOrData offset;

    public SaveRepresent(VarSymbol varSymbol, AddressOrData offset, AddressOrData source) {
        super(source);
        this.varSymbol = varSymbol;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %s[%s]",lineNumToString(),"SAVE",target.toString(),
                                               varSymbol.symbolToken.getText(),
                                               offset.toString());
    }
}
