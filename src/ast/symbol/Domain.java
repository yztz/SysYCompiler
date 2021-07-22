package ast.symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Domain {
    public static List<Function> functions = new ArrayList<>();
    private static Deque<Domain> stack = new ArrayDeque<>();
    private static Domain globalDomain = new Domain(null);
    private static Function currentFunc = null;
    private static int globalOffset = 0;

    static {
        stack.push(globalDomain);
    }

    private Domain father;
    private SymbolTable symbolTable = new SymbolTable(this);

    private Domain(Domain father) {
        this.father = father;
    }

    public static void enterDomain() {
        stack.push(new Domain(stack.peek()));
    }

    public static void leaveDomain() {
        stack.pop();
    }

    public static void enterFunc(String name, String retType) {
        Function function = new Function(name, retType);
        functions.add(function);
        currentFunc = function;
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
            globalOffset += variable.size * Variable.INT_WIDTH;
        else
            currentFunc.totalOffset += variable.size * Variable.INT_WIDTH;

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

    private static int getTotalOffset() {
        if (null == currentFunc) {
            return globalOffset;
        } else {
            return currentFunc.totalOffset;
        }
    }

    private static Domain getDomain() {
        return stack.peek();
    }

}
