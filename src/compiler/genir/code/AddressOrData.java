package compiler.genir.code;

import java.util.Objects;

public class AddressOrData {
    public AddressOrData(boolean isData, int item) {
        this.isData = isData;
        this.item = item;
    }

    public boolean isData;
    public int item;
    //public Reg reg;
    @Override
    public String toString() {
        return isData ? String.valueOf(item) :"$"+ item;
    }

    public static void resetAddressCounter()
    {
        currentAddress = 0;
    }
    static int currentAddress = 0;
    public static AddressOrData createNewAddress()
    {
        return new AddressOrData(false,currentAddress++);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressOrData that = (AddressOrData) o;
        return isData == that.isData && item == that.item;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isData, item);
    }

    public AddressOrData copy()
    {
        return new AddressOrData(isData,item);
    }
}
