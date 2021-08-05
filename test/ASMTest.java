import antlr.SysYParser;
import asm.CodeGenerator;
import ast.AstNode;
import ir.IRs;
import ir.code.IR;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class ASMTest {

    @Test
    public void testGlobalVar() {
        Utils.getIRs("test/testFull.sys");
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.genCode();
    }
    @Test
    public void testIF() {
        Utils.getIRs("test/testIf.sys");
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.genCode();
    }

    @Test
    public void testFull() {
        Utils.getIRs("test/testFull.sys");
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.genCode();
    }
    @Test
    public void testWhile() {
        Utils.getIRs("test/testWhile.sys");
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.genCode();
    }

    @Test
    public void testIO() {
        Utils.getIRs("test/testIO.sys");
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.genCode();
    }


}
