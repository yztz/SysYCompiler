package compiler.symboltable;

import compiler.symboltable.function.FuncSymbol;

public class SymbolDomain {
    public final static SymbolDomain globalDomain=new SymbolDomain(0,null,null);

    public SymbolDomain fatherDomain;
    private final FuncSymbol func;
    public SymbolTable symbolTable;
    private final int id;
    public SymbolDomain(int id,SymbolDomain fatherDomain,FuncSymbol funcSymbol) {
        this.fatherDomain = fatherDomain;
        symbolTable=new SymbolTable(this);
        func=funcSymbol;
        if(funcSymbol!=null)
            func.addDomain(this);
        this.id = id;
    }

    /**
     * 所属函数的符号
     */
    public FuncSymbol getFunc() {
        return func;
    }

    public int getId() {
        return id;
    }

    int totalOffset = 0;
    public int getTotalOffset()
    {
        if(getFunc()==null)
            return totalOffset;

        return getFunc().totalSymbolOffset;
    }

    public void appendTotalOffset(int offset)
    {
        if(getFunc()==null)
        {
            totalOffset+=offset;
        }else{
            getFunc().totalSymbolOffset+=offset;
        }
    }
}
