package common;

import asm.code.Code;

import java.io.*;


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

    public static void main(String[] args) {
        System.out.println(Long.parseLong("fffffbf8", 16));
    }

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

    /**
     * ror32 - rotate a 32-bit value right
     */
    public static int ror32(int word, int shift) {
        return (word >> (shift & 31)) | (word << ((-shift) & 31));
    }

    /*
     * Checks if immediate value can be converted to imm12(12 bits) value.
     */
    public static boolean imm8m(long x) {
        x = Math.abs(x);
        int rot;

        for (rot = 0; rot < 16; rot++)
            if ((x & ~ror32(0xff, 2 * rot)) == 0)
                return true;
        return false;
    }

    public static boolean isLegalOffset(int offset) {
        return offset >= -4095 && offset <= 4095;
    }

    public static String preprocess(BufferedReader reader)
    {
        StringBuilder builder = new StringBuilder();
        int lineNum = 0;
        try{
            String s;
            while((s = reader.readLine())!=null){//使用readLine方法，一次读一行
                lineNum++;
                s = s.replaceAll("starttime\\(\\)",String.format("_sysy_starttime(%d)",lineNum));
                s = s.replaceAll("stoptime\\(\\)",String.format("_sysy_stoptime(%d)",lineNum));
                s = s.replace("_SYSY_N","1024");
                builder.append(s).append("\n");
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }

}



