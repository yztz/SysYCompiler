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

    public static void enterFunc(String name) {
        Function function = new Function(name);
        functions.add(function);
        currentFunc = function;
    }
    public static void leaveFunc() {
        currentFunc = null;
    }

    public static void addVariable(String name, int size) {
        Variable newVar;
        if (null == currentFunc) {
            newVar = new Variable(name);    // 全局变量不知道offset是咋样
        }
        else {
            newVar = new Variable(name, currentFunc.totalOffset);
            currentFunc.totalOffset += size * Variable.INT_WIDTH;
        }
        getDomain().symbolTable.addVariable(newVar);

    }

    public static Domain getDomain() {
        return stack.peek();
    }

    public static void addVariable(String name) {
        addVariable(name, 1);
    }
}
