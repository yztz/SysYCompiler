package symboltable;

import org.antlr.v4.runtime.Token;

public class ConstSymbol extends ValueSymbol{
    public int[] constVal;

    public ConstSymbol(int val,Token symbolToken, SymbolDomain domain) {
        super(symbolToken, domain);
        constVal = new int[]{val};
    }

    public ConstSymbol(int[] val,Token symbolToken, int[] dimensions, SymbolDomain domain) {
        super(symbolToken, dimensions, domain);
        constVal = val;
    }
}
