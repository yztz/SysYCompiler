package common.symbol;

import asm.BasicBlock;
import common.ILabel;
import ir.code.IR;

import java.util.*;

public class Function implements ILabel {
    public static final Map<String, Type> STD = new HashMap<>();

    static {
        STD.put("getint", Type.INT);
        STD.put("getch", Type.INT);
        STD.put("getarray", Type.INT);
        STD.put("putint", Type.VOID);
        STD.put("putch", Type.VOID);
        STD.put("putarray", Type.VOID);
        STD.put("_sysy_starttime", Type.VOID);
        STD.put("_sysy_stoptime", Type.VOID);
        Domain.loadSTD();
    }

    public static final int PARAM_LIMIT = 4;

    public String name;
    public int totalOffset = 0;
    public Type retType;
    private List<Variable> params = new ArrayList<>();
    private List<Variable> variables = new ArrayList<>();

    public List<IR> irs = new ArrayList<>();
    public List<BasicBlock> blocks;

    public boolean isRetUsed = false;



    public Function(String name, String retType) {
        this.name = name;
        this.retType = Type.valueOf(retType.toUpperCase());

        if ("main".equals(name)) isRetUsed = true;
    }

    public Function(String name, Type retType) {
        this.name = name;
        this.retType = retType;

        if ("main".equals(name)) isRetUsed = true;
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

    public boolean isSTD() {
        return STD.containsKey(name);
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
