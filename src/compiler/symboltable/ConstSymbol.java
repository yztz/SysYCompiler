package compiler.symboltable;

import org.antlr.v4.runtime.Token;

public class ConstSymbol extends ValueSymbol{
    public int[] constVal;

    public ConstSymbol(int val,Token symbolToken) {
        super(symbolToken);
        constVal = new int[]{val};
    }

    public ConstSymbol(int[] val,Token symbolToken, int[] dimensions,boolean isArray) {
        super(symbolToken, dimensions,isArray);
        constVal = val;
    }

    @Override
    public boolean isArray() {
        return constVal.length>1;
    }

    @Override
    public int getOffsetByte() {
        return 0;
    }
}
