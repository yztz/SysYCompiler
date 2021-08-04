import antlr.SysYLexer;
import antlr.SysYParser;
import ast.AstNode;
import ast.AstVisitor;
import ir.IRParser;
import ir.IRs;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

public class Utils {
    public static AstVisitor visitor = new AstVisitor();
    public static IRParser irParser = new IRParser();

    public static void makeVisible(Parser parser, Tree tree) {
        //show AST in GUI
        JFrame frame = new JFrame("Antlr AST");
        JPanel panel = new JPanel();
        TreeViewer viewer = new TreeViewer(Arrays.asList(
                parser.getRuleNames()),tree);
//        viewer.setScale(0.8); // Scale a little
        panel.add(viewer);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        while(frame.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static SysYParser getParser(String fileName) {
        CharStream input = null;
        try {
            input = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input != null;

        SysYLexer lexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new SysYParser(tokens);
    }

    public static void getIRs(String filepath) {
        SysYParser parser = getParser(filepath);
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        irParser.flatAst(root);
    }
}
