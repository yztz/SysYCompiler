package compiler;

public class ConstDef {
    public static final int WORD_SIZE = 4;
    public static final int INT_SIZE=4;
    public static boolean armv7ve=true;
    public static boolean modPow2Optimize=true;

    public static String getArchName()
    {
        return armv7ve?"armv7ve":"armv7-a";
    }
}
