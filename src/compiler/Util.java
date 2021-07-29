package compiler;

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
        if(num.substring(0,2).toLowerCase(Locale.ROOT).equals("0x"))
        {
            return Integer.parseInt(num.replace("0x",""),16);
        }else if(num.charAt(0) == '0')
        {
            return Integer.parseInt(num.substring(1),8);
        }
        return Integer.parseInt(num);
    }
}
