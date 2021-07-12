import antlr.SysYLexer;
import antlr.SysYParser;
import genir.SysYIRListener;
import genir.code.InterRepresent;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import symboltable.FuncSymbol;
import symboltable.FuncSymbolTable;
import symboltable.SymbolTableHost;
import symboltable.SysSymbolListener;

import java.io.IOException;

public class antlrTest {


    private SysYParser getParser(String fileName)
    {
        CharStream input = null;
        try {
            input = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input!=null;

        SysYLexer lexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new SysYParser(tokens);
    }

    @Test
    public void testSymbol()
    {
        ParseTree tree = getParser("test/testSymbol.sys").compUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);

        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);

        for (InterRepresent code : irListener.irCodes.codes) {
            System.out.println(code.toString());
        }

        for (FuncSymbol funcSymbol : funcSymbolTable.funcSymbols.values()) {
            System.out.println(funcSymbol.funcName.getText()+ ":" + funcSymbol.firstStmtHolder.getInterRepresent().lineNum);
        }
    }

    @Test
    public void testExp() {
        ParseTree tree = getParser("test/testExp.sys").exp();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);

        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);
        for (InterRepresent code : irListener.irCodes.codes) {
            System.out.println(code.toString());
        }
    }

    @Test
    public void testIf()
    {
        SysYParser parser = getParser("test/testWhileIf.sys");
        ParseTree tree = parser.stmt();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);
        for (InterRepresent code : irListener.irCodes.codes) {
            System.out.println(code.toString());
        }
    }

    @Test
    public void testFunc()
    {
        SysYParser parser = getParser("test/testFunc.sys");
        ParseTree tree = parser.compUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);
        for (InterRepresent code : irListener.irCodes.codes) {
            System.out.println(code.toString());
        }
    }

    private void prepareSymbol(ParseTree tree, ParseTreeWalker walker, SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        SysSymbolListener symbolListener=new SysSymbolListener(symbolTableHost, funcSymbolTable);
        walker.walk(symbolListener, tree);
    }
}
