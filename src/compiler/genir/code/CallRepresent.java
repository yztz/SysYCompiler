package compiler.genir.code;

import compiler.asm.Address;
import compiler.symboltable.FuncSymbol;

import java.util.*;
import java.util.stream.Collectors;

public class CallRepresent extends InterRepresent{
    public AddressOrData returnResult;
    public FuncSymbol funcSymbol;
    public AddressOrData[] params;
    public CallRepresent(FuncSymbol funcSymbol) {
        this.funcSymbol = funcSymbol;
    }

    public CallRepresent(FuncSymbol funcSymbol,int targetAddress) {
        this.returnResult = new AddressOrData(false, targetAddress);
        this.funcSymbol = funcSymbol;
    }

    public CallRepresent( FuncSymbol funcSymbol, AddressOrData[] params,int targetAddress) {
        this.returnResult = new AddressOrData(false, targetAddress);
        this.funcSymbol = funcSymbol;
        this.params = params;
    }

    @Override
    public String toString() {
        String paramsStr = "";
        if(params!=null)
            paramsStr = Arrays.stream(params).map(AddressOrData::toString).collect(Collectors.joining(","));
        if(returnResult==null)
            return String.format("%s: %-7s %s(%s)",lineNumToString(),"CALL",funcSymbol.funcName.getText(),paramsStr);
        return String.format("%s: %-7s %s(%s) %-4s",lineNumToString(),"CALL",funcSymbol.funcName.getText(),
                                                paramsStr,
                                                returnResult.toString());
    }

    @Override
    public Collection<Address> getAllAddress() {
        List<Address> address = new ArrayList<>();
        if(funcSymbol.hasReturn())
            address.add(new Address(returnResult,true));

        for(int i=0;i<Math.min(4,params.length);i++)
        {
            address.add(new Address(params[i],false));
        }
        return address;
    }
}
