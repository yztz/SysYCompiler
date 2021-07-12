package genir.code;

public class AddressOrData {
    public AddressOrData(boolean isData, int item) {
        this.isData = isData;
        this.item = item;
    }

    public boolean isData;
    public int item;

    @Override
    public String toString() {
        return isData ? String.valueOf(item) :"$"+ item;
    }
}
