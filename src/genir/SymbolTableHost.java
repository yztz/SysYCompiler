package genir;


import antlr.SysYParser;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableHost {
    public static SymbolTable globalSymbolTable = new SymbolTable(null,null);
    public static Map<SysYParser.BlockContext,SymbolTable> symbolTableMap=new HashMap<>();
    public static SymbolTable getSymbolTable(SysYParser.BlockContext blockToken)
    {
        if(blockToken==null)
        {
            return globalSymbolTable;
        }
        return symbolTableMap.getOrDefault(blockToken, null);
    }
    public static SymbolTable createSymbolTable(SysYParser.BlockContext blockToken,SysYParser.FuncDefContext func)
    {
        SymbolTable table = new SymbolTable(blockToken,func);
        symbolTableMap.put(blockToken, table);
        return table;
    }
}
