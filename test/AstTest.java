import antlr.SysYLexer;
import antlr.SysYParser;
import ast.AstNode;
import ast.MyVisitor;
import ast.Utils;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

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
        AstNode root = visitor.visit(tree);
        makeVisible(parser, root);
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

    public void makeVisible(Parser parser, Tree tree) {
        //show AST in GUI
        JFrame frame = new JFrame("Antlr AST");
        JPanel panel = new JPanel();
        TreeViewer viewer = new TreeViewer(Arrays.asList(
                parser.getRuleNames()),tree);
//        viewer.setScale(1.5); // Scale a little
        panel.add(viewer);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        while(frame.isVisible()) {
            try {
                Thread.sleep(1000);//死循环中降低CPU占用
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
