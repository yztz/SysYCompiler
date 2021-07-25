package compiler.genir.code;

import compiler.symboltable.FuncSymbol;
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

    public static CallRepresent createFuncCallRepresent(FuncSymbol funcSymbol)
    {
        return new CallRepresent(funcSymbol,currentAddress++);
    }
    public static CallRepresent createFuncCallRepresent(FuncSymbol funcSymbol,AddressOrData[] params)
    {
        return new CallRepresent(funcSymbol,params,currentAddress++);
    }
    public static LoadRepresent createLoadRepresent(ValueSymbol varSymbol, AddressOrData offset)
    {
        return new LoadRepresent(varSymbol,offset,new AddressOrData(false,currentAddress++) );
    }

    public static SaveRepresent createSaveRepresent(ValueSymbol valueSymbol,AddressOrData offset,AddressOrData source)
    {
        return new SaveRepresent(valueSymbol,offset,source);
    }
    /*public static UnaryRepre createCondJumpRepresent(SysYParser.RelExpContextBase ctx)
    {

    }*/
}
