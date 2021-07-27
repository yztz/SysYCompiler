package compiler;

import java.util.Arrays;
import java.util.List;

public class Util {
    public static int[] listToIntArray(List<Integer> list,int length)
    {
        int[] ints = new int[length];
        for (int i = 0; i < Math.min(list.size(),length); i++) {
            ints[i] = list.get(i);
        }

        return ints;
    }
}
