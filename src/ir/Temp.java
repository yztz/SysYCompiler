package ir;

import ast.AstValue;

public class Temp implements AstValue {
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

    @Override
    public String getVal() {
        return toString();
    }
}
