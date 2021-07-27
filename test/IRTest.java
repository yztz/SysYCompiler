import antlr.SysYParser;
import ast.AstNode;
import ast.AstVisitor;
import ir.IRs;
import ir.PreProcessor;
import ir.IRParser;
import ir.code.IR;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.util.List;

public class IRTest {
    AstVisitor visitor = new AstVisitor();
    IRParser irParser = new IRParser();


    @Test
    public void testFULL() {
        SysYParser parser = Utils.getParser("test/testFull.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        IRs irs = irParser.flatAst(root);

        for (IR ir : irs) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIF() {
        SysYParser parser = Utils.getParser("test/testIf.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        IRs irs = irParser.flatAst(root);
        for (IR ir : irs) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testWhile() {
        SysYParser parser = Utils.getParser("test/testWhile.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        IRs irs = irParser.flatAst(root);
        for (IR ir : irs) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIFWhile() {
        SysYParser parser = Utils.getParser("test/testWhileIf.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        IRs irs = irParser.flatAst(root);
        for (IR ir : irs) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }
}
