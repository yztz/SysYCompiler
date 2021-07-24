package genir.code;

import asm.Reg;

public class AddressOrData {
    public AddressOrData(boolean isData, int item) {
        this.isData = isData;
        this.item = item;
    }

    public boolean isData;
    public int item;
    public Reg reg;
    @Override
    public String toString() {
        return isData ? String.valueOf(item) :"$"+ item;
    }
}
