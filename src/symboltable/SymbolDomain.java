package symboltable;

import java.util.List;

public class SymbolDomain {
    public final static SymbolDomain globalDomain=new SymbolDomain(null,null);

    public SymbolDomain fatherDomain;

    public SymbolTable symbolTable;

    public SymbolDomain(SymbolDomain fatherDomain,FuncSymbol funcSymbol) {
        this.fatherDomain = fatherDomain;
        symbolTable=new SymbolTable(this,funcSymbol);
    }
}
