package compiler.symboltable;

import org.antlr.v4.runtime.Token;

public abstract class HasInitSymbol extends ValueSymbol{



    // 初始数据在汇编的中的标签
    public String asmDataLabel = null;

    public int[] initValues;

    public HasInitSymbol(Token symbolToken,int[] initValues) {
        super(symbolToken);
        this.initValues= initValues;
    }

    public HasInitSymbol(Token symbolToken, int[] dimensions, boolean isArray,int[] initValues) {
        super(symbolToken, dimensions, isArray);
        this.initValues= initValues;
    }
}
