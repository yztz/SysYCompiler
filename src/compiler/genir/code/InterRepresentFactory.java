package compiler.genir.code;

import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.ValueSymbol;

public class InterRepresentFactory {
    public static int currentAddress = 0;
    public static BinocularRepre createBinocularRepresent(BinocularRepre.Opcodes opcodes, AddressOrData sourceFirst,
                                                          AddressOrData sourceSecond)
    {
        return new BinocularRepre(opcodes,sourceFirst,sourceSecond,currentAddress++);
    }

    public static UnaryRepre createUnaryRepresent(UnaryRepre.UnaryOp opcodes, AddressOrData source)
    {
        return new UnaryRepre(opcodes,source,currentAddress++);
    }

    public static CallRepresent createFuncCallRepresent(AbstractFuncSymbol funcSymbol)
    {
        return new CallRepresent(funcSymbol,currentAddress++);
    }
    public static CallRepresent createFuncCallRepresent(AbstractFuncSymbol funcSymbol, AddressOrData[] params)
    {
        return new CallRepresent(funcSymbol,params,currentAddress++);
    }
    public static LoadRepresent createLoadRepresent(ValueSymbol varSymbol, AddressOrData offset)
    {
        return new LoadRepresent(varSymbol,offset,new AddressOrData(false,currentAddress++) );
    }
    public static LAddrRepresent createLAddrRepresent(ValueSymbol varSymbol)
    {
        return new LAddrRepresent(new AddressOrData(false,currentAddress++),varSymbol );
    }

    public static SaveRepresent createSaveRepresent(ValueSymbol valueSymbol,AddressOrData offset,AddressOrData source)
    {
        return new SaveRepresent(valueSymbol,offset,source);
    }
    /*public static UnaryRepre createCondJumpRepresent(SysYParser.RelExpContextBase ctx)
    {

    }*/
}
