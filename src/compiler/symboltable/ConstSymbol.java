package compiler.symboltable;

import compiler.symboltable.initvalue.InitValue;
import org.antlr.v4.runtime.Token;

public class ConstSymbol extends HasInitSymbol{

    public ConstSymbol(InitValue val,Token symbolToken) {
        super(symbolToken,val);
    }

    public ConstSymbol(InitValue val, Token symbolToken, long[] dimensions, boolean isArray) {
        super(symbolToken, dimensions,isArray,val);
    }

    @Override
    public long getOffsetByte() {
        return 0;
    }
}
