import antlr.SysYLexer;
import antlr.SysYParser;
import compiler.genir.SysYIRListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import compiler.symboltable.*;

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

        System.out.println(irListener.irUnion.toString());

/*        for (Map<Integer,FuncSymbol> funcWithSameName : funcSymbolTable.funcSymbols.values()) {
            for (FuncSymbol value : funcWithSameName.values()) {
                System.out.println(value.funcName.getText()+ ":" + value.firstStmtHolder.getInterRepresent().lineNum);
            }
        }*/
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

        System.out.println(irListener.irUnion.toString());
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


        System.out.println(irListener.irUnion.toString());
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

        System.out.println(irListener.irUnion.toString());
    }

    @Test
    public void testFull()
    {
        SysYParser parser = getParser("test/testFull.sys");
        ParseTree tree = parser.compUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);

        System.out.println(irListener.irUnion.toString());
    }

    private void prepareSymbol(ParseTree tree, ParseTreeWalker walker, SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        SymbolScanner scanner = new SymbolScanner(symbolTableHost,funcSymbolTable);
        scanner.scanSymbol(tree);
    }
}
