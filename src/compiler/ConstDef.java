package compiler;

public class ConstDef {
    public static final int WORD_SIZE = 4;
    public static final int INT_SIZE=4;
    public static boolean armv7ve=true;
    public static boolean modPow2Optimize=true;
    public static boolean removeUselessMulDiv = false; //导致bitset 段错误
    public static boolean avoidUselessLoad = true; // 导致median1死循环
    public static boolean globalOptimize = false;
    public static String getArchName()
    {
        return armv7ve?"armv7ve":"armv7-a";
    }
}
