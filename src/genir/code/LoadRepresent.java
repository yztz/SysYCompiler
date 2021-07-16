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
        return lineNumToString()+String.format("%-7s %s[%s] %-7s","LOAD",varSymbol.varToken.getText(),
                                               offset.toString(),target.toString());
    }
}
