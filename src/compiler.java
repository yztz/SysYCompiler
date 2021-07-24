import antlr.SysYLexer;
import antlr.SysYParser;
import asm.AsmGen;
import genir.SysYIRListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import symboltable.FuncSymbolTable;
import symboltable.SymbolScanner;
import symboltable.SymbolTableHost;

import java.io.IOException;

public class compiler {
    public static void main(String[] args)
    {
        String inputFileStr = null;
        String outputFileStr = null;
        boolean optimization = false;
        String operation = null;
        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            String arg = args[i];
            switch (arg) {
                case "-S":
                    operation = arg; //也只有-S吧

                    break;
                case "-O1":
                    optimization = true;
                    break;
                case "-o":
                    outputFileStr = args[i++];
                    break;
                default:
                    inputFileStr = arg;
                    break;
            }
        }

        SysYParser parser = getParser(inputFileStr);
        ParseTree tree = parser.compUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        SymbolTableHost symbolTableHost=new SymbolTableHost();
        FuncSymbolTable funcSymbolTable=new FuncSymbolTable();
        prepareSymbol(tree, walker, symbolTableHost, funcSymbolTable);
        SysYIRListener irListener = new SysYIRListener(symbolTableHost, funcSymbolTable);
        walker.walk(irListener, tree);

        AsmGen asmGen = new AsmGen(symbolTableHost);
        String result = asmGen.generate(irListener.irUnion);

        System.out.println(result);
    }
    private static void prepareSymbol(ParseTree tree, ParseTreeWalker walker, SymbolTableHost symbolTableHost,
                                FuncSymbolTable funcSymbolTable) {
        SymbolScanner scanner = new SymbolScanner(symbolTableHost, funcSymbolTable);
        scanner.scanSymbol(tree);
    }
    private static SysYParser getParser(String fileName)
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
}
