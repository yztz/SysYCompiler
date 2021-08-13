package compiler.genir.code;

import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.ValueSymbol;

public class InterRepresentFactory {
    //public static int currentAddress = 0;
    public static BinocularRepre createBinocularRepresent(BinocularRepre.Opcodes opcodes, AddressOrData sourceFirst,
                                                          AddressOrData sourceSecond)
    {
        if(sourceFirst==null || sourceSecond==null)
            System.exit(-3);
        return new BinocularRepre(opcodes,sourceFirst,sourceSecond,AddressOrData.currentAddress++);
    }

    public static UnaryRepre createUnaryRepresent(UnaryRepre.UnaryOp opcodes, AddressOrData source)
    {
        if(source == null)
            System.exit(-4);
        return new UnaryRepre(opcodes,source,AddressOrData.currentAddress++);
    }

    public static CallRepresent createFuncCallRepresent(AbstractFuncSymbol funcSymbol)
    {
        return new CallRepresent(funcSymbol,AddressOrData.currentAddress++);
    }
    public static CallRepresent createFuncCallRepresent(AbstractFuncSymbol funcSymbol, AddressOrData[] params)
    {
        return new CallRepresent(funcSymbol,params,AddressOrData.currentAddress++);
    }
    public static LoadRepresent createLoadRepresent(ValueSymbol varSymbol, AddressOrData offset)
    {
        if(offset == null)
            System.exit(-5);
        return new LoadRepresent(varSymbol,offset,new AddressOrData(false,AddressOrData.currentAddress++) );
    }
    public static LAddrRepresent createLAddrRepresent(ValueSymbol varSymbol)
    {
        return new LAddrRepresent(new AddressOrData(false,AddressOrData.currentAddress++),varSymbol );
    }

    public static LAddrRepresent createLAddrRepresent(ValueSymbol varSymbol,AddressOrData offset)
    {
        if(offset == null)
            System.exit(-6);
        return new LAddrRepresent(new AddressOrData(false,AddressOrData.currentAddress++),varSymbol,offset);
    }

    public static SaveRepresent createSaveRepresent(ValueSymbol valueSymbol,AddressOrData offset,AddressOrData source)
    {
        if(offset == null)
            System.exit(-7);
        return new SaveRepresent(valueSymbol,offset,source);
    }

    public static RelRepresent createRelRepresent(AddressOrData left, AddressOrData right, IfGotoRepresent.RelOp op)
    {
        return new RelRepresent(AddressOrData.currentAddress++,op,left,right);
    }
    /*public static UnaryRepre createCondJumpRepresent(SysYParser.RelExpContextBase ctx)
    {

    }*/
}
