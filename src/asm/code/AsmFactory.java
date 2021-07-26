package asm.code;

import asm.RegGetter;
import asm.RegGetterImpl;
import asm.Register;
import asm.Utils;
import common.symbol.Function;

import static asm.Register.FP;
import static asm.Register.SP;

public class AsmFactory {
    private static FunContext funContext = null;


    public static Register ldr(Register register, int offset) {
        write(String.format("ldr %s, [%s, #%d]", register, FP, offset));
        return register;
    }


    public static void str(Register register, int offset) {
        write(String.format("str %s, [%s, #%d]", register, FP, offset));
    }

//    public static void param(Variable variable, Register register) {
//        write(String.format());
//    }


    private static Register loadImm(int value, Register register) {
        write(String.format("mov %s %d", register, value));
        return register;
    }


    public static void enterFunc(Function function) {
        funContext = new FunContext(function);
    }

    public static void leaveFunc() {
        if (funContext.existCall)
            write(String.format("push {%s, %s}", FP, SP));
        else
            write(String.format("push {%s}", FP));

        funContext = null;
    }




    private static void write(String code) {
        if (null == funContext) {
            Utils.write(code);
        } else {
            funContext.write(code);
        }
    }


    static class FunContext {
        private final StringBuilder codes = new StringBuilder();

        Function function;
        boolean existCall;

        public FunContext(Function function) {
            this.function = function;
        }

        public void write(String code) {
            this.codes.append(code).append('\n');
        }

        public void emit() {
            Utils.write(codes.toString());
        }
    }
}
