package compiler.genir.code;

import compiler.asm.AddressRWInfo;
import compiler.asm.Reg;
import compiler.asm.Regs;
import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;
import java.util.stream.Collectors;

public class CallRepresent extends InterRepresent{
    public AddressOrData returnResult;
    public AbstractFuncSymbol funcSymbol;
    public AddressOrData[] params;
    public CallRepresent(AbstractFuncSymbol funcSymbol) {
        this.funcSymbol = funcSymbol;
    }

    public CallRepresent(AbstractFuncSymbol funcSymbol,int targetAddress) {
        this.returnResult = new AddressOrData(false, targetAddress);
        this.funcSymbol = funcSymbol;
    }

    public CallRepresent( AbstractFuncSymbol funcSymbol, AddressOrData[] params,int targetAddress) {
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
            return String.format("%s: %-7s %s(%s)",lineNumToString(),"CALL",funcSymbol.getFuncName(),paramsStr);
        return String.format("%s: %-7s %s(%s) %-4s",lineNumToString(),"CALL",funcSymbol.getFuncName(),
                                                paramsStr,
                                                returnResult.toString());
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        List<AddressRWInfo> addressRWInfos = new ArrayList<>();
        addressRWInfos.add(new AddressRWInfo(returnResult, true));

        for(int i=0;i<(params==null?0:params.length);i++)
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

    @Override
    public InterRepresent createCopy() {
        AddressOrData[] copyParams = new AddressOrData[params.length];
        for (int i = 0; i < params.length; i++) {
            copyParams[i] = params[i].copy();
        }
        return new CallRepresent(funcSymbol,copyParams,returnResult.item);
    }
}
