package genir.code;

import antlr.SysYParser;

public class InterRepresentFactory {
    public static int currentAddress = 0;
    public static BinocularRepre createBinocularRepresent(BinocularRepre.Opcodes opcodes,AddressOrNum sourceFirst,
                                                      AddressOrNum sourceSecond)
    {
        return new BinocularRepre(opcodes,sourceFirst,sourceSecond,currentAddress++);
    }

    public static UnaryRepre createUnaryRepresent(UnaryRepre.UnaryOp opcodes, AddressOrNum source)
    {
        return new UnaryRepre(opcodes,source,currentAddress++);
    }

    /*public static UnaryRepre createCondJumpRepresent(SysYParser.RelExpContextBase ctx)
    {

    }*/
}
