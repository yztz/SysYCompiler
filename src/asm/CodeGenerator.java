package asm;

import asm.allocator.Reference;
import asm.code.AsmFactory;
import asm.code.Code;
import ast.IAstValue;
import common.Utils;
import common.symbol.Domain;
import common.symbol.Function;
import common.symbol.SymbolTable;
import common.symbol.Variable;
import ir.IRs;
import ir.code.IR;


import java.util.*;

public class CodeGenerator {
    private List<Code> codes = new LinkedList<>();

    public void genCode() {
        // 全局分析
        analyze();
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

    private void analyze() {
//        analyzeFuncRetInfo();
    }

    private void analyzeFuncRetInfo() {
        Map<Function, Set<Function>> retDeps = new HashMap<>();
        List<BasicBlock> blocks = IRs.blocks;

        for (BasicBlock block : blocks) {
            for (IR ir : block.getIRs()) {
                Map<IName, Reference> refMap = ir.refMap;
                // 分析函数返回值使用情况
                if (ir.isCall()) {
                    Function function = ((Function) ir.op2);
                    if (function.hasReturn() && !function.isRetUsed) {
                        IName ret = ((IName) ir.op1);
                        Reference reference = refMap.get(ret);
                        if (reference.nextRef != null) {
                            if (reference.nextRef.isReturn()) {
                                if (function != block.function) {
                                    Set<Function> deps = retDeps.getOrDefault(function, new HashSet<>());
                                    deps.add(block.function);
                                    retDeps.put(function, deps);
                                } else {
                                    function.isRetUsed = true;
                                    retDeps.remove(function);
                                }
                            } else {
                                function.isRetUsed = true;
                                retDeps.remove(function);
                            }
                        }
                    }
                }
            }
        }
        for (Function function : retDeps.keySet()) {
            Deque<Function> dependencies = new ArrayDeque<>(retDeps.get(function));
            while (!dependencies.isEmpty()) {
                Function top = dependencies.pop();
                if (retDeps.containsKey(top)) { //依赖于其他函数
                    dependencies.addAll(retDeps.get(top));
                } else if (top.isRetUsed){
                    function.isRetUsed = true;
                    retDeps.remove(function);
                    break;
                }
            }
        }

        for (Function function : IRs.getFunctions()) {
            System.out.printf("%s:%s\t\tretUsed = %s%n", function.name, function.retType, function.isRetUsed);
        }
    }

    private void genInfo() {
        addCode(AsmFactory.arch("armv7ve"));
    }

    private void genGlobalVar() {
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
