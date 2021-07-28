package compiler.genir;

import compiler.symboltable.function.FuncSymbol;


public class IRFunction extends IRCollection {
    public FuncSymbol funcSymbol;

    public IRFunction(FuncSymbol funcSymbol) {
        this.funcSymbol = funcSymbol;
    }
}
