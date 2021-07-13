package genir.code;

import symboltable.FuncSymbol;
import symboltable.VarSymbol;

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
    public static LoadRepresent createLoadRepresent(VarSymbol varSymbol,AddressOrData offset)
    {
        return new LoadRepresent(varSymbol,offset,currentAddress++);
    }

    public static SaveRepresent createSaveRepresent(VarSymbol varSymbol,AddressOrData offset,AddressOrData source)
    {
        return new SaveRepresent(varSymbol,offset,source);
    }
    /*public static UnaryRepre createCondJumpRepresent(SysYParser.RelExpContextBase ctx)
    {

    }*/
}
