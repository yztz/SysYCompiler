package asm;

import ast.AstNode;
import common.symbol.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static asm.Register.*;

public class RegGetterImpl implements RegGetter {
    private Map<Register, Set<Variable>> regDesc = new HashMap<>();

    public static final Register[] availReg = {r4, r5, r6, r7, r8, r9, r10};

    public RegGetterImpl() {
        for (Register register : availReg) regDesc.put(register, new HashSet<>());
    }

    @Override
    public Register getReg(Variable var) {
        Register ret = find(var);   // 寻找已存在R
        if (null != ret) return ret;
        ret = getFreeReg(); // 寻找空R
        if (null != ret)
            return ret;
        else
            System.err.println("寄存器分配失败");
        return null;
    }

    @Override
    public Register getReg() {
        return getFreeReg();
    }

    @Override
    public void freeReg(Register register) {
        regDesc.get(register).clear();
    }

    private Register find(Variable variable) {
        for (Register register : regDesc.keySet()) {
            Set<Variable> set = regDesc.get(register);
            if (set.contains(variable)) return register;
        }
        return null;
    }

    private Register getFreeReg() {
        for (Register register : availReg) {
            Set<Variable> set = regDesc.get(register);
            if (set.isEmpty()) return register;
        }

        return null;
    }

//    private int getCost(Register register) {
//        Set<Variable>
//
//        int cost = 0;
//        int n = regDesc.get(register).size();
//        int i = 1;
//        for ()
//    }
}
