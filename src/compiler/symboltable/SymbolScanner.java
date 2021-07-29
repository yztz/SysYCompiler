package compiler.symboltable;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class SymbolScanner {
    SymbolTableHost symbolTableHost;
    FuncSymbolTable funcSymbolTable;

    public SymbolScanner(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        this.symbolTableHost = symbolTableHost;
        this.funcSymbolTable = funcSymbolTable;
    }

    public void scanSymbol(ParseTree parseTree)
    {
        ParseTreeWalker walker = new ParseTreeWalker();
        SysExpCalListener expListener = new SysExpCalListener(symbolTableHost);
        walker.walk(expListener,parseTree);

        SysFuncConstSymbolListener constSymbolListener = new SysFuncConstSymbolListener(symbolTableHost, funcSymbolTable);
        walker.walk(constSymbolListener,parseTree);

        walker.walk(expListener,parseTree); //把常量考虑进来，再算一遍

        SysVarListener symbolListener = new SysVarListener(symbolTableHost, funcSymbolTable);
        walker.walk(symbolListener,parseTree);
    }
}
