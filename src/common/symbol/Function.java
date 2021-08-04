package common.symbol;

import asm.allocator.BasicBlock;
import ast.IAstValue;
import common.ILabel;
import ir.code.IR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Function implements ILabel {
    public static final int PARAM_LIMIT = 4;

    public String name;
    public int totalOffset = 0;
    public Type retType;
    private List<Variable> params = new ArrayList<>();
    private List<Variable> variables = new ArrayList<>();

    public List<IR> irs = new ArrayList<>();
    public List<BasicBlock> blocks;


    public Function(String name, String retType) {
        this.name = name;
        this.retType = Type.valueOf(retType.toUpperCase());
    }

    public void addParam(Variable param) {
        param.paramIndex = params.size();
        params.add(param);
        if (param.paramIndex < PARAM_LIMIT) {
            param.offset = totalOffset;
            totalOffset += param.width;
        } else {
            param.offset = param.width * (PARAM_LIMIT - param.paramIndex - 1);
        }

    }

    public void addVariable(Variable variable) {
        variables.add(variable);
        if (variable.isArray) {
            totalOffset += variable.getBytes();
            variable.offset = totalOffset - variable.width;
        } else {
            variable.offset = totalOffset;
            totalOffset += variable.getBytes();
        }

    }

    public List<Variable> getParams() {
        return Collections.unmodifiableList(params);
    }

    public Variable getParam(int i) {
        return params.get(i);
    }

    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public int getParamBytes() {
        int ret = 0;
        for (Variable var : params) {
            ret += var.width;
        }
        return ret;
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
