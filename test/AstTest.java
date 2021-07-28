import antlr.SysYParser;
import ast.AstNode;
import ast.AstVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

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
    public void testFull() {
        SysYParser parser = Utils.getParser("test/testFull.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        Utils.makeVisible(parser, root);
    }







}
