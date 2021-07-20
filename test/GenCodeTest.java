import gencode.code.Code;
import gencode.code.CodeFactory;
import gencode.code.operand.LocWithOffset;
import gencode.code.operand.Register;
import gencode.code.operand.Immediate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenCodeTest {

    @Test
    public void testCode() {
        System.out.print(CodeFactory.sub(Register.r0, Register.r1, 0).setLabel(CodeFactory.getLabel()));
        System.out.print(CodeFactory.mul(Register.r0, Register.r1, 0));
        System.out.print(CodeFactory.div(Register.r0, Register.r1, 2).setLabel(CodeFactory.getLabel()));
        System.out.print(CodeFactory.str(Register.r1, new LocWithOffset(Register.r0, 10)));
        System.out.print(CodeFactory.ldr(Register.r1, new LocWithOffset(Register.r0, 10)));
        System.out.print(CodeFactory.push(Register.r1, Register.r2));
        System.out.print(CodeFactory.cmp(Register.r1, 1));
    }
}
