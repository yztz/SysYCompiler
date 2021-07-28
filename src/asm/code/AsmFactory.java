package asm.code;

import common.Register;
import asm.Utils;
import common.symbol.Function;


public class AsmFactory {

    public static String ldr(Register register, int offset) {
        return String.format("\tldr %s, [fp, #%d]", register, offset);
    }


    public static String str(Register register, int offset) {
        return String.format("\tstr %s, [fp, #%d]", register, offset);
    }


    public static String loadImm(int value, Register register) {
        return String.format("\tmov %s %d", register, value);
    }

    public static String add(Register rd, Register rn, Register rm) {
        return String.format("\tadd %s, %s, %s", rd, rn, rm);
    }

    public static String add(Register rd, Register rn, int imm) {
        return String.format("\tadd %s, %s, #%d", rd, rn, imm);
    }

    public static String sub(Register rd, Register rn, Register rm) {
        return String.format("\tsub %s, %s, %s", rd, rn, rm);
    }

    public static String sub(Register rd, Register rn, int imm) {
        return String.format("\tsub %s, %s, #%d", rd, rn, imm);
    }

    public static String label(String label) {
        return String.format("%s:", label);
    }




//    public static void enterFunc(Function function) {
//        funContext = new FunContext(function);
//    }
//
//    public static void leaveFunc() {
//        // 输出上下文内容
//        funContext.emit();
//
//        funContext = null;
//    }




//    private static void write(String code) {
//        code = "\t" + code;
//        if (null == funContext) {   // 输出
//            Utils.write(code);
//        } else {    // 输出到函数上下文
//            funContext.write(code);
//        }
//    }



}
