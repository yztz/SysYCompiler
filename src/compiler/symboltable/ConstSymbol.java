package compiler.symboltable;

import org.antlr.v4.runtime.Token;

public class ConstSymbol extends HasInitSymbol{

    public ConstSymbol(int val,Token symbolToken) {
        super(symbolToken,new int[]{val});
    }

    public ConstSymbol(int[] val,Token symbolToken, int[] dimensions,boolean isArray) {
        super(symbolToken, dimensions,isArray,val);
    }

    @Override
    public int getOffsetByte() {
        return 0;
    }
}
