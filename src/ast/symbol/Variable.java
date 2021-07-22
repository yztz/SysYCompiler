package ast.symbol;

import ast.AstValue;

import java.util.ArrayList;
import java.util.List;

public class Variable implements AstValue {
    public static final int INT_WIDTH = 4;

    public String name;
    public int offset;
    public Domain domain;
    public boolean isConst;
    public boolean isArray;
    public List<Integer> dimensions;
    public int size;
    public int[] value;

    public int pos = 0; //用于数组初始化赋值

    public Variable(String name, int offset, Domain domain, boolean isConst, boolean isArray, int size) {
        this.name = name;
        this.offset = offset;
        this.domain = domain;
        this.isConst = isConst;
        this.isArray = isArray;
        this.size = size;
        this.value = new int[size];
    }

    public static Variable var(String name, int offset, Domain domain) {
        return new Variable(name, offset, domain, false, false, 1);
    }

    public static Variable constVar(String name, int offset, Domain domain) {
        return new Variable(name, offset, domain, true, false, 1);
    }

    public static Variable array(String name, int offset, Domain domain, List<Integer> dimensions) {
        int size = 1;
        for (int num : dimensions) size *= num;
        Variable newVar = new Variable(name, offset, domain, false, true, size);
        newVar.dimensions = new ArrayList<>(dimensions);
        return newVar;
    }

    public static Variable constArray(String name, int offset, Domain domain, List<Integer> dimensions) {
        Variable newVar = array(name, offset, domain, dimensions);
        newVar.isConst = true;
        return newVar;
    }

    @Override
    public String getVal() {
        return name;
    }
}
