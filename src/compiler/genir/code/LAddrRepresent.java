package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;

public class LAddrRepresent extends WrittenRepresent{
    public ValueSymbol valueSymbol;

    public LAddrRepresent(int targetAddress, ValueSymbol valueSymbol) {
        super(targetAddress);
        this.valueSymbol = valueSymbol;
    }

    public LAddrRepresent(AddressOrData target, ValueSymbol valueSymbol) {
        super(target);
        this.valueSymbol = valueSymbol;
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        return super.getAllAddressRWInfo();
    }

    @Override
    public String toString() {
        return String.format("%s: %-7s %-4s %s",lineNumToString(),
                             "LADDR",target.toString(),valueSymbol.symbolToken.getText());
    }
}
