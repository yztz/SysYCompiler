package ast.symbol;

import ast.Value;

public class Variable implements Value {
    public static final int INT_WIDTH = 4;

    public String name;
    public int offset;
    public Domain domain;
    public boolean isConst = false;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    @Override
    public String getVal() {
        return name;
    }
}
