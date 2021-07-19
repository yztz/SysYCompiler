package symboltable;

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
        SysConstExpListener expListener = new SysConstExpListener();
        walker.walk(expListener,parseTree);

        SysSymbolListener symbolListener = new SysSymbolListener(symbolTableHost,funcSymbolTable);
        walker.walk(symbolListener,parseTree);
    }
}
