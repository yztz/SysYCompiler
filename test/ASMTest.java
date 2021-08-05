import asm.CodeGenerator;
import ir.IRs;
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


}
