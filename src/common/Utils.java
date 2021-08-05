package common;

import asm.code.Code;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;


public class Utils {

    private static PrintStream out;

//    static {
//        try {
////            out = new FileWriter("./out.s");
//            out = new PrintStream(new FileOutputStream("./out.s"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void openStream(String file) {
        try {
            out = new PrintStream(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(Code code) {
        if (!code.isLabel) {
            out.println("\t" + code);
        } else {
            out.println(code + ":");
        }
    }

    public static int align8(int i) {
        if (i % 8 == 0) return i;
        return i + 8 - i % 8;
    }

}
