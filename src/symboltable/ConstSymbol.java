package symboltable;

import org.antlr.v4.runtime.Token;

public class ConstSymbol extends ValueSymbol{
    public int[] constVal;

    public ConstSymbol(int val,Token symbolToken) {
        super(symbolToken);
        constVal = new int[]{val};
    }

    public ConstSymbol(int[] val,Token symbolToken, int[] dimensions) {
        super(symbolToken, dimensions);
        constVal = val;
    }
}
