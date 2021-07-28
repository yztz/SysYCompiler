package ir;

import asm.IName;

public class Temp implements IName {
    private static int next_id = 0;


    public final int id;
    private Temp(int id) {
        this.id = id;
    }

    public static Temp newTmp() {
        return new Temp(next_id++);
    }

    @Override
    public String toString() {
        return "$" + id;
    }

}
