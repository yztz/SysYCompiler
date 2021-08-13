package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.symboltable.VarSymbol;

import java.util.Collection;
import java.util.Collections;

public class InitVarRepresent extends InterRepresent{
    public VarSymbol varSymbol;

    public InitVarRepresent(VarSymbol varSymbol) {
        this.varSymbol = varSymbol;
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        return Collections.emptyList();
    }

    @Override
    public InterRepresent createCopy() {
        return new InitVarRepresent(varSymbol);
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %s[%d]",lineNumToString(),"DEC",varSymbol.symbolToken.getText(),
                             varSymbol.getLength());
    }
}
