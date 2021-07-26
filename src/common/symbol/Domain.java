package common.symbol;

import java.util.*;

public class Domain {
    public static Map<String, Function> functions = new HashMap<>();
    private static Deque<Domain> stack = new ArrayDeque<>();
    public static Domain globalDomain = new Domain(null);
    private static Function currentFunc = null;
    private static int globalOffset = 0;

    private static Domain nextDomain = null;

    static {
        stack.push(globalDomain);
    }

    private Domain father;
    private SymbolTable symbolTable = new SymbolTable(this);

    private Domain(Domain father) {
        this.father = father;
    }

    public static void enterDomain() {
        if (null == nextDomain){
            stack.push(new Domain(stack.peek()));
        } else {
            stack.push(nextDomain);
            nextDomain = null;
        }
    }

    public static void leaveDomain() {
        stack.pop();
    }

    public static Function enterFunc(String name, String retType) {
        if (functions.containsKey(name)) {
            System.err.println("Function [" + name + "] redeclared");
            System.exit(-1);
        }
        Function function = new Function(name, retType);
        functions.put(name, function);
        currentFunc = function;
        nextDomain = new Domain(stack.peek());

        return function;
    }

    public static void leaveFunc() {
        currentFunc = null;
    }


    public static Variable addConstVar(String name) {
        return addVariableToTable(Variable.constVar(name, getTotalOffset(), getDomain()));
    }

    public static Variable addConstArray(String name, List<Integer> dimensions) {
        return addVariableToTable(Variable.constArray(name, getTotalOffset(), getDomain(), dimensions));
    }


    public static Variable addArray(String name, List<Integer> dimensions) {
        return addVariableToTable(Variable.array(name, getTotalOffset(), getDomain(), dimensions));
    }

    public static Variable addVariable(String name) {
        return addVariableToTable(Variable.var(name, getTotalOffset(), getDomain()));
    }


    private static Variable addVariableToTable(Variable variable) {
        if (null != getDomain().symbolTable.searchSymbol(variable.name)) {
            System.err.println("Variable [" + variable.name + "] redeclared");
            return null;
        }

        // 添加变量到对应的符号表
        getDomain().symbolTable.addVariable(variable);
        // 增加总偏移量
        if (null == currentFunc)
            globalOffset += variable.size * variable.width;
        else
            currentFunc.totalOffset += variable.size * variable.width;

        return variable;
    }


    public static Variable searchVar(String name) {
        return searchVar(name, getDomain());
    }

    public static Variable searchVar(String name, Domain domain) {
        Domain curr = domain;
        while (null != curr) {
            Variable variable = curr.symbolTable.searchSymbol(name);
            if (null != variable)
                return variable;
            else
                curr = curr.father;
        }
        System.err.println("variable [" + name + "] is undefined");
        return null;
    }

    public static Function searchFunc(String name) {
        if (functions.containsKey(name)) return functions.get(name);
        else {
            System.err.println("function [" + name + "] is undefined");
            return null;
        }
    }

    private static int getTotalOffset() {
        if (null == currentFunc) {
            return globalOffset;
        } else {
            return currentFunc.totalOffset;
        }
    }

    public static Domain getDomain() {
        if (null == nextDomain)
             return stack.peek();
        else
            return nextDomain;
    }

}
