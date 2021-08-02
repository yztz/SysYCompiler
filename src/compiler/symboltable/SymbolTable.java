package compiler.symboltable;


import compiler.ConstDef;
import compiler.genir.code.AddressOrData;
import compiler.symboltable.initvalue.InitValue;
import org.antlr.v4.runtime.Token;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolDomain domain;

    private final Map<String,ValueSymbol> symbols = new HashMap<>();
    public SymbolTable(SymbolDomain domain) {
        this.domain = domain;
    }


    /**
     * 获取该符号表的作用域
     * @return 作用域context、可能为代码块，当为null是为全局符号表
     */
    public SymbolDomain getDomain() {
        return domain;
    }


    /**
     * 向符号表中登记符号
     * @param token 符号
     */
    public VarSymbol addVarArray(Token token, int[] dimensions, InitValue initValues) //类型只有一个int
    {
        VarSymbol symbol = new VarSymbol(getCurrentOffset(), token, dimensions, initValues,true);
        symbols.put(token.getText(), symbol);
        appendOffset(symbol.length*ConstDef.INT_SIZE);
        return symbol;
    }

    public VarSymbol addVar(Token token, InitValue val) //类型只有一个int
    {
        VarSymbol symbol = new VarSymbol(getCurrentOffset(), token, val);
        symbols.put(token.getText(), symbol);
        appendOffset(symbol.length*ConstDef.INT_SIZE);
        return symbol;
    }
    public ParamSymbol addParam(Token token)
    {
        if(domain.getFunc()==null)
            return null;

        ParamSymbol symbol = new ParamSymbol(token);
        symbols.put(token.getText(), symbol);
        symbol.offsetByte = getCurrentOffset();
        appendOffset(ConstDef.INT_SIZE); //参数都是4字节的（数组是指针）

        domain.getFunc().paramSymbols.add(symbol);
        return symbol;
    }

    public ParamSymbol addParamArray(Token token, AddressOrData[] dim)
    {
        if(domain.getFunc()==null)
            return null;

        ParamSymbol symbol = new ParamSymbol(token,dim,true);
        symbols.put(token.getText(), symbol);
        symbol.offsetByte = getCurrentOffset();
        appendOffset(ConstDef.INT_SIZE);

        domain.getFunc().paramSymbols.add(symbol);
        return symbol;
    }
    public ConstSymbol addConst(Token token,InitValue constValue)
    {
        ConstSymbol symbol = new ConstSymbol(constValue,token);
        symbols.put(token.getText(), symbol);
        return symbol;
    }

    public ConstSymbol addConstArray(Token token, int[] dim, InitValue constValues)
    {
        ConstSymbol symbol = new ConstSymbol(constValues, token,dim,true);
        symbols.put(token.getText(), symbol);
        return symbol;
    }
    private int getCurrentOffset()
    {
        return domain.getTotalOffset();
    }
    private void appendOffset(long byteSize)
    {
        domain.appendTotalOffset(byteSize);
    }
    public Collection<ValueSymbol> getAllSymbol()
    {
        return symbols.values();
    }
    public boolean containSymbol(String ident)
    {
        return symbols.containsKey(ident);
    }

    public ValueSymbol getSymbol(String ident)
    {
        return symbols.getOrDefault(ident, null);
    }
}
