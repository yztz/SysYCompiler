package genir.code;

import symboltable.VarSymbol;

public class LoadRepresent extends WrittenRepresent{
    public VarSymbol varSymbol;
    public AddressOrData offset;

    public LoadRepresent(VarSymbol varSymbol, AddressOrData offset,int targetAddress) {
        super(targetAddress);
        this.varSymbol = varSymbol;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %s[%s] %-7s",lineNumToString(),"LOAD",varSymbol.symbolToken.getText(),
                                               offset.toString(),target.toString());
    }
}
