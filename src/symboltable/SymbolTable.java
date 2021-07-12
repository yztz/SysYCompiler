package symboltable;


import antlr.SysYParser;
import com.sun.istack.internal.Nullable;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private final SymbolDomain domain;
    private final FuncSymbol func;
    private int totalOffset=0;
    private final Map<String,VarSymbol> offsets=new HashMap<>();
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
    public void addSymbol(Token token,int[] dimensions) //类型只有一个int
    {
        offsets.put(token.getText(),new VarSymbol(totalOffset,token,dimensions));
        totalOffset+=4;
    }

    public void addSymbol(Token token) //类型只有一个int
    {
        offsets.put(token.getText(),new VarSymbol(totalOffset,token));
        totalOffset+=4;
    }

    public void addParam(Token token)
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token);
        symbol.isFuncParam = true;
        offsets.put(token.getText(),symbol);
        totalOffset+=4;
    }

    public void addParam(Token token,int[] dim)
    {
        VarSymbol symbol = new VarSymbol(totalOffset, token,dim);
        symbol.isFuncParam = true;
        offsets.put(token.getText(),symbol);
        totalOffset+=4;
    }

    /**
     * 返回符号相对的偏移量
     * @param token 符号token
     * @return 偏移量，-1表示符号不存在
     */
    public int getSymbolOffset(String ident)
    {
        if(offsets.containsKey(ident))
            return offsets.get(ident).offset;
        return -1;
    }

    public boolean containSymbol(String ident)
    {
        return offsets.containsKey(ident);
    }

    public VarSymbol getSymbol(String ident)
    {
        return offsets.getOrDefault(ident,null);
    }
}
