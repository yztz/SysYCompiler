package gencode.code;

import gencode.code.interfaces.Memory;
import gencode.code.interfaces.RegOrImm;
import gencode.code.operand.Immediate;
import gencode.code.operand.Register;


public class CodeFactory {
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "div";
    public static final String LDR = "ldr";
    public static final String STR = "str";
    public static final String PUSH = "push";
    public static final String POP = "pop";


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
        return new BinaryCode<>(LDR, rd, operand2);
    }

    public static Code str(Register rd, Memory operand2) {
        return new BinaryCode<>(false, STR, rd, operand2);
    }

    public static Code push(Register... regList) {
        return new UnaryCode<>(PUSH, regList);

    }


}
