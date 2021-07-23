package ast.symbol;

import ast.AstValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variable implements AstValue {
    public static final int INT_WIDTH = 4;

    public String name;
    public int offset;
    public Domain domain;
    public boolean isConst;
    public boolean isArray;
    public List<Integer> dimensions;
    public int size;
    public Map<Integer, Integer> constVal = new HashMap<>();

    public int pos = 0; //用于数组初始化赋值

    public Variable(String name, int offset, Domain domain, boolean isConst, boolean isArray, int size) {
        this.name = name;
        this.offset = offset;
        this.domain = domain;
        this.isConst = isConst;
        this.isArray = isArray;
        this.size = size;
    }

    public void addConstVal(int val) {
        this.constVal.put(pos++, val);
    }

    public boolean isCollapsible() {
        return isConst && domain == Domain.globalDomain;
    }

    public int indexConstVal(int idx) {
        return constVal.getOrDefault(idx, 0);
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
        newVar.dimensions = dimensions;
        return newVar;
    }

    public static Variable constArray(String name, int offset, Domain domain, List<Integer> dimensions) {
        Variable newVar = array(name, offset, domain, dimensions);
        newVar.isConst = true;
        return newVar;
    }

//    public static Variable paramVar(String name) {
//        return new Variable(name, -1, null, false, false, 1);
//    }
//
//    public static Variable paramArray(String name, List<Integer> dimensions) {
//        return array(name, -1, null, dimensions);
//    }

    @Override
    public String toString() {
        String h = name + " = ";
        if (isArray) {
            StringBuilder sb = new StringBuilder(h).append('[').append(indexConstVal(0));
            for (int i = 1; i < size; i++) {
                sb.append(", ").append(indexConstVal(i));
            }
            return sb.append(']').toString();
        } else {
            return h + indexConstVal(0);
        }
    }

    @Override
    public String getVal() {
        return name;
    }
}
