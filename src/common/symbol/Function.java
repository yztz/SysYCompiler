package common.symbol;

import ast.AstValue;

import java.util.Map;

public class Function implements AstValue {
    public String name;
    public int totalOffset = 0;
    public String retType;
    public Map<String, Variable> params;

    public Function(String name, String retType) {
        this.name = name;
        this.retType = retType;
    }

    public void addParam(Variable param) {
        params.put(param.name, param);
    }

    @Override
    public String getVal() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
