package genir.code;

public class ReturnRepresent extends InterRepresent{
    public AddressOrData returnData;

    public ReturnRepresent() {
    }

    public ReturnRepresent(AddressOrData returnData) {
        this();
        this.returnData = returnData;
    }

    @Override
    public String toString() {
        if(returnData!=null)
        {
            return String.format("%s: %-7s %-4s",lineNumToString(),"RET",returnData.toString());
        }
        return String.format("%s: %-7s",lineNumToString(),"RET");
    }
}
