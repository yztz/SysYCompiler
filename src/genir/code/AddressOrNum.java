package genir.code;

public class AddressOrNum {
    public AddressOrNum(boolean isNum, int data) {
        this.isNum = isNum;
        this.data = data;
    }

    public boolean isNum;
    public int data;

    @Override
    public String toString() {
        return isNum? String.valueOf(data) :"("+data+")";
    }
}
