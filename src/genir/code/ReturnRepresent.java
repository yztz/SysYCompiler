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
            return lineNumToString()+String.format("%-7s %-4s","RET",returnData.toString());
        }
        return lineNumToString()+"RET";
    }
}
