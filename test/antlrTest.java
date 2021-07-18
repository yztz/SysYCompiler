import antlr.SysYLexer;
import antlr.SysYParser;
import gencode.Address;
import gencode.CodeGenerator;
import gencode.Ref;
import genir.IRCode;
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
import java.util.List;
import java.util.Map;

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
        for (InterRepresent code : getIR("test/testExp.sys")) {
            System.out.println(code.toString());
        }
    }

    @Test
    public void testIf()
    {
        for (InterRepresent code : getIR("test/testWhileIf.sys")) {
            System.out.println(code.toString());
        }
    }

    public List<InterRepresent> getIR(String filepath) {
        SysYParser parser = getParser(filepath);
        ParseTree tree = parser.compUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);

        return irListener.irCodes.codes;
    }

    @Test
    public void testFunc()
    {
        for (InterRepresent code : getIR("test/testFunc.sys")) {
            System.out.println(code.toString());
        }
    }

    @Test
    public void testFull()
    {
        for (InterRepresent code : getIR("test/testFull.sys")) {
            System.out.println(code.toString());
        }
    }

    @Test
    public void testArray() {
        for (InterRepresent code : getIR("test/testArray.sys")) {
            System.out.println(code.toString());
        }
    }

    private void prepareSymbol(ParseTree tree, ParseTreeWalker walker, SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        SysSymbolListener symbolListener=new SysSymbolListener(symbolTableHost, funcSymbolTable);
        walker.walk(symbolListener, tree);
    }

    @Test
    public void testRef() {
        SysYParser parser = getParser("test/ref.sys");
        ParseTree tree = parser.compUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
        SysYIRListener irListener = new SysYIRListener(symbolTableHost,funcSymbolTable);
        walker.walk(irListener, tree);

        CodeGenerator codeGenerator = new CodeGenerator(irListener.irCodes, symbolTableHost);
        codeGenerator.genCode();
        for (InterRepresent code : irListener.irCodes.codes) {
            System.out.println(code.toString());
            for (Address address : code.refMap.keySet()) {
                Ref ref = code.refMap.get(address);
                System.out.println("\t" + address + ": " + ref);
            }
        }
    }
}
