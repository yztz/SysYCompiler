import antlr.SysYLexer;
import antlr.SysYParser;
import ast.AstNode;
import ast.AstVisitor;
import ast.OP;
import ast.Optimizer;
import ir.AstParser;
import ir.code.IR;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.io.IOException;

public class IRTest {
    AstVisitor visitor = new AstVisitor();
    AstParser irParser = new AstParser();


    @Test
    public void testFULL() {
        SysYParser parser = Utils.getParser("test/testFull.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        irParser.parseAst(root);

        for (IR ir : irParser.getIRCode()) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIF() {
        SysYParser parser = Utils.getParser("test/testWhileIf.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        Optimizer.processIF(root);
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testWhile() {
        SysYParser parser = Utils.getParser("test/testWhile.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        Optimizer.processWhile(root);
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIFWhile() {
        SysYParser parser = Utils.getParser("test/testWhile.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);

        Optimizer.processWhile(root);
        Optimizer.processIF(root);
        Utils.makeVisible(parser, root);
    }
}
