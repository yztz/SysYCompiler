package symboltable;


import org.antlr.v4.runtime.Token;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolDomain domain;
    private int totalOffset=0;
    private final Map<String,ValueSymbol> symbols = new HashMap<>();
    public SymbolTable(SymbolDomain domain) {
        this.domain = domain;
    }

    public int getTotalOffset() {
        return totalOffset;
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
    public VarSymbol addVar(Token token, int[] dimensions, int[] initValues) //类型只有一个int
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token, dimensions, initValues);
        symbols.put(token.getText(), symbol);
        totalOffset+=symbol.length*4;
        return symbol;
    }

    public VarSymbol addVar(Token token, int[] initValues) //类型只有一个int
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token, initValues);
        symbols.put(token.getText(), symbol);
        totalOffset+=symbol.length*4;
        return symbol;
    }
    private int paramOffset = 0;
    public void addParam(Token token)
    {
        ParamSymbol symbol = new ParamSymbol(token);
        symbols.put(token.getText(), symbol);
        symbol.offsetByte = paramOffset;
        paramOffset+=symbol.length*4;
        //totalOffset+=4;
    }

    public void addParam(Token token,int[] dim)
    {
        ParamSymbol symbol = new ParamSymbol(token,dim);
        symbols.put(token.getText(), symbol);
        symbol.offsetByte = paramOffset;
        paramOffset+=symbol.length*4;
    }
    public void addConst(Token token,int constValue)
    {
        ConstSymbol symbol = new ConstSymbol(constValue,token);
        symbols.put(token.getText(), symbol);
    }

    public void addConst(Token token,int[] dim,int[] constValues)
    {
        ConstSymbol symbol = new ConstSymbol(constValues, token,dim);
        symbols.put(token.getText(), symbol);
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
