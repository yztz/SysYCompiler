package ast.symbol;

import ast.AstValue;

public class Function implements AstValue {
    public String name;
    public int totalOffset = 0;
    public String retType;

    public Function(String name, String retType) {
        this.name = name;
        this.retType = retType;
    }

    @Override
    public String getVal() {
        return name;
    }
}
