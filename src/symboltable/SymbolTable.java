package symboltable;


import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolDomain domain;
    private final FuncSymbol func;
    private int totalOffset=0;
    private final Map<String,ValueSymbol> symbols = new HashMap<>();
    public SymbolTable(SymbolDomain domain,FuncSymbol func) {
        this.domain = domain;
        this.func = func;
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
    public void addSymbol(Token token,int[] dimensions,int[] initValues) //类型只有一个int
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token, dimensions, initValues, domain);
        symbols.put(token.getText(), symbol);
        totalOffset+=symbol.length*4;
    }

    public void addSymbol(Token token,int[] initValues, SymbolDomain domain) //类型只有一个int
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token, initValues, domain);
        symbols.put(token.getText(), symbol);
        totalOffset+=symbol.length*4;
    }

    public void addParam(Token token)
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token , null, domain);
        symbol.isFuncParam = true;
        symbols.put(token.getText(), symbol);
        //totalOffset+=4;
    }

    public void addParam(Token token,int[] dim)
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token,dim,null);
        symbol.isFuncParam = true;
        symbols.put(token.getText(), symbol);
        //totalOffset+=4;
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

    public boolean containSymbol(String ident)
    {
        return symbols.containsKey(ident);
    }

    public ValueSymbol getSymbol(String ident)
    {
        return symbols.getOrDefault(ident, null);
    }
}
