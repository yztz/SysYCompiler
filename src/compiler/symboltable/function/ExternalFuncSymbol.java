package compiler.symboltable.function;

import compiler.symboltable.BType;
import org.antlr.v4.runtime.Token;

public class ExternalFuncSymbol extends AbstractFuncSymbol{
    public ExternalFuncSymbol(String funcName) {
        this.funcName = funcName;
    }

    private String funcName;
    @Override
    public String getFuncName() {
        return funcName;
    }
}
