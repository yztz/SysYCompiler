package compiler.symboltable;

import compiler.ConstDef;
import org.antlr.v4.runtime.Token;

public class ParamSymbol extends ValueSymbol{
    public int offsetByte = 0;
    public ParamSymbol(Token symbolToken) {
        super(symbolToken);
    }

    public ParamSymbol(Token symbolToken, int[] dimensions,boolean isArray) {
        super(symbolToken, dimensions,isArray);
        array = true;
    }

    private boolean array;
    @Override
    public boolean isArray() {
        return array;
    }

    @Override
    public int getOffsetByte() {
        return offsetByte;
    }

    @Override
    public int getByteSize() {
        return ConstDef.WORD_SIZE;
    }
}
