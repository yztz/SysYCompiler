package asm;

import asm.code.AsmFactory;
import asm.code.Code;
import common.Utils;
import common.symbol.Domain;
import common.symbol.Function;
import common.symbol.SymbolTable;
import common.symbol.Variable;
import ir.IRs;


import java.util.*;

public class CodeGenerator {
    private List<Code> codes = new LinkedList<>();

    public void genCode() {
        // 生成头部信息
        genInfo();
        // 初始化全局变量
        genGlobalVar();
        // 生成函数
        addCode(AsmFactory.code(".text"));
        for (Function function : IRs.getFunctions())
            addCode(new FunctionContext(function).codes);
        output();
    }

    public void genInfo() {
        addCode(AsmFactory.arch("armv7ve"));
    }

    public void genGlobalVar() {
        SymbolTable globalSymTab = Domain.globalDomain.symbolTable;
        if (!globalSymTab.normalVariable.isEmpty())
            addCode(AsmFactory.section(".data"));
        for (Variable variable : globalSymTab.normalVariable) {
//            variable.printInitVals();
            addCode(AsmFactory.var(variable));
        }
        if (!globalSymTab.constVariable.isEmpty())
            addCode(AsmFactory.section(".rodata"));
        for (Variable variable : globalSymTab.constVariable) {
            addCode(AsmFactory.var(variable));
        }
    }



    private void addCode(Code code) {
        codes.add(code);
//        Utils.write(code);
    }
    private void addCode(List<Code> codes) {
//        for (Code code : codes) {
//            Utils.write(code);
//        }
        this.codes.addAll(codes);
    }

    private void output(){
        for (Code code : codes) Utils.write(code);
    }

}
