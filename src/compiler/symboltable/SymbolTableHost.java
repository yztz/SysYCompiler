package compiler.symboltable;



import compiler.symboltable.function.FuncSymbol;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SymbolTableHost {
    public SymbolTable getGlobalSymbolTable()
    {
        return SymbolDomain.globalDomain.symbolTable;
    }
    public Map<SymbolDomain,SymbolTable> symbolTableMap=new HashMap<>();
    public SymbolTable getSymbolTable(SymbolDomain symbolDomain)
    {
        return symbolTableMap.getOrDefault(symbolDomain, null);
    }

    private final Map<FuncSymbol,Integer> funcDomainNumMap = new HashMap<>();
    /**
     * 创建一个符号表
     * @param fatherDomain 父作用域(不是当前作用域)
     * @param func 所属函数
     * @return 符号表
     */
    public SymbolDomain createSymbolDomain( SymbolDomain fatherDomain,
                                           FuncSymbol func)
    {
        int domainIndex = funcDomainNumMap.getOrDefault(func,0);
        SymbolDomain currentDomain = new SymbolDomain(domainIndex,fatherDomain,func);
        symbolTableMap.put(currentDomain, currentDomain.symbolTable);
        funcDomainNumMap.put(func,domainIndex+1);
        return currentDomain;
    }

    /**
     * 搜索变量符号
     * @param startDomain 开始的作用域
     * @param symbolToken 符号Token
     * @return 符号，无则返回null
     */
    
    public VarSymbol searchVarSymbol(SymbolDomain startDomain, Token symbolToken)
    {
        String ident = symbolToken.getText();
        SymbolDomain domain = startDomain;
        VarSymbol result = null;
        while (domain!=null)
        {
            if (domain.symbolTable.containSymbol(ident)) {
                ValueSymbol varSymbol = domain.symbolTable.getSymbol(ident);
                if(varSymbol instanceof VarSymbol && varSymbol.symbolToken.getStartIndex()<=symbolToken.getStartIndex())
                    //检查是否在使用前定义
                {
                    result=(VarSymbol) varSymbol;
                    break;
                }
            }

            domain=domain.fatherDomain;
        }

        return result;
    }

    public ParamSymbol searchParamSymbol(SymbolDomain startDomain, Token symbolToken)
    {
        String ident = symbolToken.getText();
        SymbolDomain domain = startDomain;
        ParamSymbol result = null;
        while (domain!=null)
        {
            if (domain.symbolTable.containSymbol(ident)) {
                ValueSymbol varSymbol = domain.symbolTable.getSymbol(ident);
                if(varSymbol instanceof ParamSymbol &&varSymbol.symbolToken.getStartIndex()<=symbolToken.getStartIndex())
                //检查是否在使用前定义
                {
                    result=(ParamSymbol) varSymbol;
                    break;
                }
            }

            domain=domain.fatherDomain;
        }

        return result;
    }
    public ValueSymbol searchSymbol(SymbolDomain startDomain,
    Token symbolToken, Function<ValueSymbol,Boolean> filter)
    {
        String ident = symbolToken.getText();
        SymbolDomain domain = startDomain;
        ValueSymbol result = null;
        while (domain!=null)
        {
            if (domain.symbolTable.containSymbol(ident)) {
                ValueSymbol varSymbol = domain.symbolTable.getSymbol(ident);
                if(varSymbol.symbolToken.getStartIndex()<=symbolToken.getStartIndex() &&
                        (filter==null || filter.apply(varSymbol)))
                //检查是否在使用前定义
                {
                    result=varSymbol;
                    break;
                }
            }

            domain=domain.fatherDomain;
        }

        return result;
    }
    public ValueSymbol searchSymbol(SymbolDomain startDomain, Token symbolToken)
    {
        String ident = symbolToken.getText();
        SymbolDomain domain = startDomain;
        ValueSymbol result = null;
        while (domain!=null)
        {
            if (domain.symbolTable.containSymbol(ident)) {
                ValueSymbol varSymbol = domain.symbolTable.getSymbol(ident);
                if(varSymbol.symbolToken.getStartIndex()<=symbolToken.getStartIndex())
                //检查是否在使用前定义
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
