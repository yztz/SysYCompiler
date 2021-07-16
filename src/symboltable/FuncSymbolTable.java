package symboltable;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncSymbolTable {
    private int defineOrder = 0;
    public final Map<String,FuncSymbol> funcSymbols=new HashMap<>();

    public FuncSymbol addFunc(Token funcName,int paramNum,BType returnType)
    {
        FuncSymbol funcSymbol = new FuncSymbol(funcName, defineOrder, paramNum, returnType);
        funcSymbols.put(funcName.getText(),funcSymbol);
        defineOrder++;
        return funcSymbol;
    }

    public FuncSymbol getFuncSymbol(String funcName)
    {
        return funcSymbols.get(funcName);
    }
}
