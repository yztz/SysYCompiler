package common.symbol;

import asm.IName;

import java.util.*;

public class Variable implements IName {
    public static final int INT_WIDTH = 4;

    public Type type = Type.INT;
    public String name;
    public int offset;
    public Domain domain;
    public boolean isConst;
    public boolean isArray;
    public List<Integer> dimensions;
    public int size;
    public int width = INT_WIDTH;
    public boolean isInit = true;
    public Map<Integer, Integer> constVal = new HashMap<>();
    public boolean isParam;
    public int paramIndex = -1;
    public int pos = 0; //用于数组初始化赋值

    public void printInitVals() {
        System.out.println("====================" + name + "=====================");
        int zeroCount = 0;
        for (int i = 0; i < size; i++) {
            int val = constVal.getOrDefault(i, 0);
            if (0 == val) zeroCount++;
            else {
                if (zeroCount != 0)
                    System.out.printf("\"0[x%d]\": %d%n", zeroCount, val);

                zeroCount = 0;
                System.out.printf("\"%d\": %d%n", i, val);
            }
        }
    }

    public Variable(String name, Domain domain, boolean isConst, boolean isArray, int size) {
        this.name = name;
        this.domain = domain;
        this.isConst = isConst;
        this.isArray = isArray;
        this.size = size;


    }

    public SymbolTable getSymbolTable() {
        return this.domain.symbolTable;
    }

    public void addConstVal(int val) {
        this.constVal.put(pos, val);
    }

    public boolean isCollapsible() {
        // todo 常量数组待商榷
        return isConst && (isGlobal() || isInit );
    }

    public int getBytes() {
        return size * INT_WIDTH;
    }

    public int indexConstVal(int idx) {
        return constVal.getOrDefault(idx, 0);
    }

    public static Variable var(String name, Domain domain) {
        return new Variable(name, domain, false, false, 1);
    }

    public static Variable constVar(String name, Domain domain) {
        return new Variable(name, domain, true, false, 1);
    }

    public static Variable array(String name, Domain domain, List<Integer> dimensions) {
        int size = 1;
        for (int num : dimensions) size *= num;
        Variable newVar = new Variable(name, domain, false, true, size);
        newVar.dimensions = dimensions;
        return newVar;
    }

    public static Variable constArray(String name, Domain domain, List<Integer> dimensions) {
        Variable newVar = array(name, domain, dimensions);
        newVar.isConst = true;
        return newVar;
    }

    public boolean isGlobal() {
        return this.domain == Domain.globalDomain;
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
        return name;
//        String h = name + " = ";
//        if (isArray) {
//            StringBuilder sb = new StringBuilder(h).append('[').append(indexConstVal(0));
//            for (int i = 1; i < size; i++) {
//                sb.append(", ").append(indexConstVal(i));
//            }
//            return sb.append(']').toString();
//        } else {
//            return h + indexConstVal(0);
//        }
    }

}
