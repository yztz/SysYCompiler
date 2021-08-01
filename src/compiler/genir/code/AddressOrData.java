package compiler.genir.code;

import compiler.asm.Reg;

public class AddressOrData {
    public AddressOrData(boolean isData, long item) {
        this.isData = isData;
        this.item = item;
    }

    public boolean isData;
    public long item;
    //public Reg reg;
    @Override
    public String toString() {
        return isData ? String.valueOf(item) :"$"+ item;
    }
}
