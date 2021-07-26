import antlr.SysYLexer;
import antlr.SysYParser;
import ast.AstNode;
import ast.AstVisitor;
import ast.Pass;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

public class AstTest {
    AstVisitor visitor = new AstVisitor();
    @Test
    public void testExp() {
        SysYParser parser = Utils.getParser("test/testExp.sys");
        ParseTree tree = parser.exp();
        AstNode root = visitor.visit(tree);
//        Utils.interpreterAst(root);
    }
    @Test
    public void testConst() {
        SysYParser parser = Utils.getParser("test/testFull.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIFPass() {
        SysYParser parser = Utils.getParser("test/testWhileIf.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        Pass.pass1(root);
        Utils.makeVisible(parser, root);
    }




}
