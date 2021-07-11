import antlr.SysYBaseListener;
import antlr.SysYLexer;
import antlr.SysYParser;
import genir.SymbolTableHost;
import genir.SysSymbolListener;
import genir.SysYIRListener;
import genir.code.InterRepresent;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

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
    public void testExp() {
        ParseTree tree = getParser("test/testExp.sys").exp();
        ParseTreeWalker walker = new ParseTreeWalker();
        SysYIRListener irListener = new SysYIRListener();
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
        SysYIRListener irListener = new SysYIRListener();
        walker.walk(irListener, tree);
        for (InterRepresent code : irListener.irCodes.codes) {
            System.out.println(code.toString());
        }
    }
}
