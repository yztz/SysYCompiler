package symboltable;


import antlr.SysYParser;
import com.sun.istack.internal.Nullable;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableHost {
    public Map<SymbolDomain,SymbolTable> symbolTableMap=new HashMap<>();
    public SymbolTable getSymbolTable(SymbolDomain symbolDomain)
    {
        return symbolTableMap.getOrDefault(symbolDomain, null);
    }

    /**
     * 创建一个符号表
     * @param fatherDomain 父作用域(不是当前作用域)
     * @param func 所属函数
     * @return 符号表
     */
    public SymbolDomain createSymbolDomain(@Nullable SymbolDomain fatherDomain,
                                           FuncSymbol func)
    {
        SymbolDomain currentDomain = new SymbolDomain(fatherDomain,func);
        symbolTableMap.put(currentDomain, currentDomain.symbolTable);
        return currentDomain;
    }

    @Nullable
    public VarSymbol searchSymbol(SymbolDomain startDomain,Token symbolToken)
    {
        String ident = symbolToken.getText();
        SymbolDomain domain = startDomain;
        VarSymbol result = null;
        while (domain!=null)
        {
            if (domain.symbolTable.containSymbol(ident)) {
                VarSymbol varSymbol = domain.symbolTable.getSymbol(ident);
                if(varSymbol.varToken.getStartIndex()<=symbolToken.getStartIndex()) //检查是否在使用前定义
                {
                    result=varSymbol;
                    break;
                }
            }

            domain=domain.fatherDomain;
        }

        return result;
    }
}
