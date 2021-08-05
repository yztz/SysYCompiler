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

    public void genCode() {
        // 生成头部信息
        genInfo();
        // 初始化全局变量
        genGlobalVar();
        // 生成函数
        write(AsmFactory.code(".text"));
        for (Function function : IRs.getFunctions())
            write(new FunctionContext(function).codes);
    }

    public void genInfo() {
        write(AsmFactory.arch("armv7ve"));
    }

    public void genGlobalVar() {
        SymbolTable globalSymTab = Domain.globalDomain.symbolTable;
        if (!globalSymTab.normalVariable.isEmpty())
            write(AsmFactory.section(".data"));
        for (Variable variable : globalSymTab.normalVariable) {
            write(AsmFactory.var(variable));
        }
        if (!globalSymTab.constVariable.isEmpty())
            write(AsmFactory.section(".rodata"));
        for (Variable variable : globalSymTab.constVariable) {
            write(AsmFactory.var(variable));
        }
    }


    private void write(Code code) {
        Utils.write(code);
    }
    private void write(List<Code> codes) {
        for (Code code : codes) {
            Utils.write(code);
        }
    }

}
