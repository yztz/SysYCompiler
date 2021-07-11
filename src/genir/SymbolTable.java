package genir;


import antlr.SysYParser;
import com.sun.istack.internal.Nullable;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private final SysYParser.BlockContext domain;
    private final SysYParser.FuncDefContext func;
    private int totalOffset=0;
    private final Map<Token,Integer> offsets=new HashMap<>();
    public SymbolTable(SysYParser.BlockContext domain,SysYParser.FuncDefContext func) {
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
    public SysYParser.BlockContext getDomain() {
        return domain;
    }


    /**
     * 向符号表中登记符号
     * @param token 符号
     */
    public void addSymbol(Token token) //类型只有一个int
    {
        offsets.put(token,totalOffset+=4);
        totalOffset+=4;
    }

    /**
     * 返回符号相对的偏移量
     * @param token 符号token
     * @return 偏移量，-1表示符号不存在
     */
    public int getSymbolOffset(Token token)
    {
        if(offsets.containsKey(token))
            return offsets.get(token);
        return -1;
    }

    public boolean containSymbol(Token token)
    {
        return offsets.containsKey(token);
    }
}
