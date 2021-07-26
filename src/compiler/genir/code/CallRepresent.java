package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.asm.Reg;
import compiler.asm.Regs;
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
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        List<AddressRWInfo> addressRWInfos = new ArrayList<>();
        if(funcSymbol.hasReturn())
            addressRWInfos.add(new AddressRWInfo(returnResult, true));

        for(int i=0;i<Math.min(4,params.length);i++)
        {
            addressRWInfos.add(new AddressRWInfo(params[i], false));
        }
        return addressRWInfos;
    }

    @Override
    public Reg addressMapRule(AddressOrData address) {
        if(address==returnResult)
            return Regs.R0;

        for (int i = 0; i < Math.min(params.length,4); i++) {
            if(params[i]==address)
                return Regs.REGS[i];
        }
        return super.addressMapRule(address);
    }
}
