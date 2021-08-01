package compiler.symboltable;

import compiler.symboltable.function.FuncSymbol;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public abstract class ValueSymbol {
    public Token symbolToken;


    //private Map<FuncSymbol,Integer> indexInFuncDataMap = new HashMap<>();
    public ValueSymbol(Token symbolToken) {
        this.symbolToken = symbolToken;
    }

    public ValueSymbol(Token symbolToken,boolean isArray) {
        this.symbolToken = symbolToken;
        this.isArray = isArray;
    }

    private boolean isArray = false;
    public boolean isArray(){
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public abstract long getOffsetByte();

    public abstract long getByteSize();
}
