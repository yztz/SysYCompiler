import antlr.SysYLexer;
import antlr.SysYParser;
import ast.AstNode;
import ast.MyVisitor;
import ast.Utils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.io.IOException;

public class AstTest {
    MyVisitor visitor = new MyVisitor();
    @Test
    public void testExp() {
        SysYParser parser = getParser("test/testExp.sys");
        ParseTree tree = parser.exp();
        AstNode root = visitor.visit(tree);
        Utils.interpreterAst(root);
    }
    @Test
    public void testConst() {
        SysYParser parser = getParser("test/testConst.sys");
        ParseTree tree = parser.compUnit();
        visitor.visit(tree);
    }

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
}
