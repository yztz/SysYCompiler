package compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Util {
    public static int[] listToIntArray(List<Integer> list,int length)
    {
        int[] ints = new int[length];
        for (int i = 0; i < Math.min(list.size(),length); i++) {
            ints[i] = list.get(i);
        }

        return ints;
    }

    public static int getIntFromStr(String num)
    {
        boolean neg = false;
        if(num.startsWith("-"))
        {
            num = num.substring(1);
            neg = true;
        }else if(num.startsWith("+"))
        {
            num = num.substring(1);
        }
        if(num.startsWith("0x") || num.startsWith("0X"))
        {
            return (int)Long.parseLong(num.toLowerCase(Locale.ROOT).replace("0x", ""), 16);
        }else if(num.startsWith("0") && num.length()>1)
        {
            return (int)Long.parseLong(num.toLowerCase(Locale.ROOT).substring(1),8);
        }
        int i = (int)Long.parseLong(num);
        return neg?-i:i;
    }

    public static void printStackAndExit(int code,Exception e)
    {
        e.printStackTrace();
        System.exit(code);
    }

    static int[] pow2 = new int[]{1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,
        32768,65536,131072,262144,524288,1048576};
    public static boolean isPow2(int num)
    {
        return Arrays.stream(pow2).anyMatch(i->i==num);
    }

    public static int getPow2(int num)
    {
        for (int i = 0; i < pow2.length; i++) {
            if(pow2[i]==num)
                return i;
        }

        return -1;
    }
}
