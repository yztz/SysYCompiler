package gencode.code;

import com.sun.xml.internal.bind.v2.model.core.ID;
import gencode.code.interfaces.Memory;
import gencode.code.interfaces.RegOrImm;
import gencode.code.operand.Immediate;
import gencode.code.operand.Register;


public class CodeFactory {
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "div";
    public static final String MOD = "mod";
    public static final String LDR = "ldr";
    public static final String STR = "str";
    public static final String PUSH = "push";
    public static final String POP = "pop";
    public static final String CMP = "cmp";


    private static int nextLabelID = 0;

    public static String getLabel() {
        return ".L" + nextLabelID++;
    }

    public static Code add(Register rd, Register operand1, RegOrImm operand2) {
        return new TernaryCode<>(ADD, rd, operand1, operand2);
    }

    public static Code add(Register rd, Register operand1, int imm) {
        return add(rd, operand1, new Immediate(imm));
    }

    public static Code sub(Register rd, Register operand1, RegOrImm operand2) {
        return new TernaryCode<>(SUB, rd, operand1, operand2);
    }

    public static Code sub(Register rd, Register operand1, int imm) {
        return sub(rd, operand1, new Immediate(imm));
    }

    public static Code mul(Register rd, Register operand1, RegOrImm operand2) {
        return new TernaryCode<>(MUL, rd, operand1, operand2);
    }

    public static Code mul(Register rd, Register operand1, int imm) {
        return mul(rd, operand1, new Immediate(imm));
    }

    public static Code div(Register rd, Register operand1, RegOrImm operand2) {
        return new TernaryCode<>(DIV, rd, operand1, operand2);
    }

    public static Code div(Register rd, Register operand1, int imm) {
        return div(rd, operand1, new Immediate(imm));
    }

    public static Code ldr(Register rd, Memory operand2) {
        return new BinaryCode<>(false, LDR, rd, operand2);
    }

    public static Code str(Register rd, Memory operand2) {
        return new BinaryCode<>(false, STR, rd, operand2);
    }

    public static Code push(Register... regList) {
        return new UnaryCode<>(PUSH, regList);
    }

    public static Code pop(Register... regList) {
        return new UnaryCode<>(POP, regList);
    }

    public static Code cmp(Register rn, RegOrImm operand2) {
        return new BinaryCode<>(false, CMP, rn, operand2);
    }
    public static Code cmp(Register rn, int operand2) {
        return cmp(rn, new Immediate(operand2));
    }


}
