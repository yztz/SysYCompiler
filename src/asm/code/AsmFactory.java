package asm.code;

import common.Register;
import asm.Utils;
import common.symbol.Function;
import common.symbol.Variable;

import java.util.ArrayList;
import java.util.List;


public class AsmFactory {

    public static Code mov(Register reg1, Register reg2) {
        return Code.code(String.format("mov %s, %s", reg1, reg2));
    }

    public static Code mov(Register reg1, int imm) {
        return Code.code(String.format("ldr %s, =0x%s", reg1, Integer.toHexString(imm)));
    }

    public static Code ldrFromStack(Register register, int offset) {
        return Code.code(String.format("ldr %s, [fp, #%d]", register, offset));
    }

    public static Code lea(Register register, String label) {
        return Code.code(String.format("ldr %s, =%s", register, label));
    }

    public static Code lsl(Register register, int val) {
        return Code.code(String.format("lsl %s, %s, #%d", register, register, val));
    }

    public static Code ldrWithoutOffset(Register rd, Register rt) {
        return Code.code(String.format("ldr %s, [%s]", rd, rt));
    }

    public static Code ldrWithoutOffset(Register rd, String rt) {
        return Code.code(String.format("ldr %s, [%s]", rd, rt));
    }

    public static Code ldrFromRegWithOffset(Register rd, Register rt, Register offset) {
        return Code.code(String.format("ldr %s, [%s, %s, lsl #2]", rd, rt, offset));
    }

    public static Code ldrFromRegWithOffset(Register rd, Register rt, int offset) {
        return Code.code(String.format("ldr %s, [%s, #%d]", rd, rt, offset));
    }

    public static Code strStack(Register register, int offset) {
        return Code.code(String.format("str %s, [fp, #%d]", register, offset));
    }

    public static Code strWithoutOffset(Register rn, Register rd) {
        return Code.code(String.format("str %s, [%s]", rn, rd));
    }

    public static Code strWithOffset(Register rn, Register rd, int offset) {
        return Code.code(String.format("str %s, [%s, #%d]", rn, rd, offset));
    }

    public static Code strWithRegOffset(Register rn, Register rd, Register offset) {
        return Code.code(String.format("str %s, [%s, %s, lsl #2]", rn, rd, offset));
    }

    public static Code cmp(Register rn, Register rm) {
        return Code.code(String.format("cmp %s, %s", rn, rm));
    }


    public static Code cmp(Register rn, int imm) {
        return Code.code(String.format("cmp %s, #%d", rn, imm));
    }

    public static Code movWhen(Register rd, int imm, String cond) {
        return Code.code(String.format("mov%s %s, #%d", cond, rd, imm));
    }


    public static Code b(String target) {
        return Code.code(String.format("b %s", target));
    }

    public static Code bl(String target) {
        return Code.code(String.format("bl %s", target));
    }

    public static Code bWhen(String target, String cond) {
        return Code.code(String.format("b%s %s", cond, target));
    }

    public static Code mul(Register rd, Register rm, Register rs) {
        return Code.code(String.format("mul %s, %s, %s", rd, rm, rs));
    }

    public static Code div(Register rd, Register rm, Register rs) {
        return Code.code(String.format("sdiv %s, %s, %s", rd, rm, rs));
    }

    public static Code mls(Register rd, Register rm, Register rs, Register rn) {
        return Code.code(String.format("mls %s, %s, %s, %s", rd, rm, rs, rn));
    }

    public static Code add(Register rd, Register rn, Register rm) {
        return Code.code(String.format("add %s, %s, %s", rd, rn, rm));
    }

    public static Code add(Register rd, Register rn, int imm) {
        return Code.code(String.format("add %s, %s, #%d", rd, rn, imm));
    }

    public static Code rsb(Register rd, Register rn, int imm) {
        return Code.code(String.format("rsb %s, %s, #%d", rd, rn, imm));
    }

    public static Code sub(Register rd, Register rn, Register rm) {
        return Code.code(String.format("sub %s, %s, %s", rd, rn, rm));
    }

    public static Code sub(Register rd, Register rn, int imm) {
        return Code.code(String.format("sub %s, %s, #%d", rd, rn, imm));
    }

    public static Code global(String name) {
        return Code.code(String.format(".global %s", name));
    }

    public static Code common(String name, int bytes) {
        return Code.code(String.format(".common %s, %d, 4", name, bytes));
    }

    public static Code mvn(Register register, int imm8m) {
        return Code.code(String.format("mvn %s, #%d", register, imm8m));
    }

    public static Code word(int value) {
        return Code.code(String.format(".word %d", value));
    }

    public static Code word(String value) {
        return Code.code(String.format(".word %s", value));
    }

    public static Code align(int value) {
        return Code.code(String.format(".align %d", value));
    }

    public static Code size(String name, String bytes) {
        return Code.code(String.format(".size %s, %s", name, bytes));
    }

    public static Code size(String name, int bytes) {
        return Code.code(String.format(".size %s, %d", name, bytes));
    }

    public static Code section(String name) {
        return Code.code(String.format(".section %s", name));
    }

    public static Code type(String name, String type) {
        return Code.code(String.format(".type %s, %%%s", name, type));
    }

    public static Code code(String content) {
        return Code.code(content);
    }

    public static Code label(String name) {
        return Code.label(name);
    }

    public static Code space(int bytes) {
        return Code.code(String.format(".space %d", bytes));
    }

    public static Code bx(Register reg) {
        return Code.code(String.format("bx %s", reg));
    }

    public static Code uxtb(Register target) {
        return Code.code(String.format("uxtb %s, %s", target, target));
    }

    public static Code arch(String arch) {
        return Code.code(String.format(".arch %s", arch));
    }

    public static Code syntax(String syntax) {
        return Code.code(String.format(".syntax %s", syntax));
    }

    public static Code fpu(String fpu) {
        return Code.code(String.format(".fpu %s", fpu));
    }

    public static Code arm() {
        return Code.code(".arm");
    }

    //===============================集成==============================//

    public static List<Code> var(Variable variable) {
        List<Code> codes = new ArrayList<>();
        if (variable.isInit) {
            codes.add(global(variable.name));
            codes.add(align(2));
            codes.add(type(variable.name, "object"));
            codes.add(size(variable.name, variable.getBytes()));
            codes.add(Code.label(variable.name));
            for (int i = 0; i < variable.size; ) {
                int val = variable.indexConstVal(i);
//                System.out.println(val);
                if (val == 0) {
                    int count = 0;
                    while (variable.indexConstVal(i) == 0 && i < variable.size) {
                        count++;
                        i++;
                    }
                    codes.add(space(count * variable.width));
                } else {
                    i++;
                    codes.add(word(val));
                }
            }
        } else {
            codes.add(common(variable.name, variable.getBytes()));
        }
        return codes;
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
