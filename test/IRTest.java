import antlr.SysYParser;
import asm.allocator.BasicBlock;
import asm.allocator.RegisterAllocator;
import ast.AstNode;
import ast.AstVisitor;
import ir.*;
import ir.code.IR;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class IRTest {
    AstVisitor visitor = new AstVisitor();
    IRParser irParser = new IRParser();


    @Test
    public void testFULL() {
        SysYParser parser = Utils.getParser("test/testFull.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        irParser.flatAst(root);

        for (IR ir : IRs.getIRs()) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIF() {
        SysYParser parser = Utils.getParser("test/testIf.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
//        irParser.flatAst(root);
//        for (IR ir : IRs.getIRs()) {
//            System.out.println(ir);
//        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testWhile() {
        SysYParser parser = Utils.getParser("test/testWhile.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        irParser.flatAst(root);
        for (IR ir : IRs.getIRs()) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }

    @Test
    public void testIFWhile() {
        SysYParser parser = Utils.getParser("test/testWhileIf.sys");
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
        irParser.flatAst(root);
        for (IR ir : IRs.getIRs()) {
            System.out.println(ir);
        }
        Utils.makeVisible(parser, root);
    }
//
//    @Test
//    public void testBasicBlock() {
//        RegisterAllocator allocator = new RegisterAllocator(genIR("test/testFull.sys"));
//        for (BasicBlock block : allocator.blocks) {
//            block.printBlock();
//        }
//    }



    public void genIR(String filepath) {
        SysYParser parser = Utils.getParser(filepath);
        ParseTree tree = parser.compUnit();
        AstNode root = visitor.visit(tree);
    }
}
