package symboltable;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncSymbolTable {
    private int defineOrder = 0;
    public final Map<String,Map<Integer,FuncSymbol>> funcSymbols=new HashMap<>();

    public FuncSymbol addFunc(Token funcName,int paramNum,BType returnType)
    {
        if (!funcSymbols.containsKey(funcName.getText())) {
            funcSymbols.put(funcName.getText(),new HashMap<>());
        }
        Map<Integer, FuncSymbol> funcThisName = funcSymbols.get(funcName.getText());
        FuncSymbol funcSymbol = new FuncSymbol(funcName, defineOrder, paramNum,funcThisName.size(), returnType);

        funcThisName.put(paramNum, funcSymbol);
        defineOrder++;
        return funcSymbol;
    }

    public FuncSymbol getFuncSymbol(String funcName,int paramNum)
    {
        if (!funcSymbols.containsKey(funcName)) {
            return null;
        }
        return funcSymbols.get(funcName).get(paramNum);
    }
}
