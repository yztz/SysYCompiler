package common.symbol;

import ast.IAstValue;
import common.ILabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Function implements ILabel {
    public String name;
    public int totalOffset = 0;
    public Type retType;
    private List<Variable> params = new ArrayList<>();

    public boolean existCall = false;


    public Function(String name, String retType) {
        this.name = name;
        this.retType = Type.valueOf(retType.toUpperCase());
    }

    public void addParam(Variable param) {
        params.add(param);
    }

    public List<Variable> getParams() {
        return Collections.unmodifiableList(params);
    }

    public Variable getParam(int i) {
        return params.get(i);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean hasReturn() {
        return Type.INT == retType;
    }

    @Override
    public String getLabelName() {
        return name;
    }
}
