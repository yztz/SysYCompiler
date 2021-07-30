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

    public boolean isAllZero()
    {
        if(initValues==null) return true;
        for (int i = 0; i < initValues.length; i++) {
            if(initValues[i]!=0)
                return false;
        }
        return true;
    }

    public int getZeroTailLength()
    {
        if(initValues==null) return getLength();
        int length = 0;
        for (int i = initValues.length - 1; i >= 0; i--) {
            if(initValues[i]!=0)
                break;
            length++;
        }
        return length;
    }
}
