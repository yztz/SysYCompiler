package symboltable;

import org.antlr.v4.runtime.Token;

public class ParamSymbol extends ValueSymbol{
    public ParamSymbol(Token symbolToken) {
        super(symbolToken);
    }

    public ParamSymbol(Token symbolToken, int[] dimensions) {
        super(symbolToken, dimensions);
    }
}
