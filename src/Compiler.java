import antlr.SysYLexer;
import antlr.SysYParser;
import asm.CodeGenerator;
import ast.AstNode;
import ast.AstVisitor;
import common.Utils;
import ir.IRParser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.antlr.v4.runtime.tree.ParseTree;




import java.io.IOException;



public class Compiler {

    public static void main(String[] args) {
        String file = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-S":
                    break;
                case "-o":
                    Utils.openStream(args[i + 1]);
                    i = i + 1;
                    break;
                default:
                    file = arg;
            }
        }
        SysYParser parser = getParser(file);
        AstVisitor visitor = new AstVisitor();
        IRParser irParser = new IRParser();
        CodeGenerator codeGenerator = new CodeGenerator();

        ParseTree tree = parser.compUnit();

        AstNode root = visitor.visit(tree);

        irParser.flatAst(root);
        //makeVisible(parser, root);
        codeGenerator.genCode();
    }

//    public static void makeVisible(Parser parser, Tree tree) {
//        //show AST in GUI
//        JFrame frame = new JFrame("Antlr AST");
//        JPanel panel = new JPanel();
//        TreeViewer viewer = new TreeViewer(Arrays.asList(
//                parser.getRuleNames()),tree);
////        viewer.setScale(0.8); // Scale a little
//        panel.add(viewer);
//        frame.add(panel);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//        while(frame.isVisible()) {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

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

}
