package compiler.symboltable;

import compiler.ConstDef;
import compiler.genir.IRCollection;
import compiler.genir.code.AddressOrData;
import org.antlr.v4.runtime.Token;

public class ParamSymbol extends ValueSymbol{
    public int offsetByte = 0;
    public IRCollection irToCalDimSize;
    public AddressOrData[] dimensions;
    public ParamSymbol(Token symbolToken) {
        super(symbolToken);
    }

    public ParamSymbol(Token symbolToken, AddressOrData[] dimensions, boolean isArray) {
        super(symbolToken,isArray);
        array = true;
        this.dimensions = dimensions;
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


    public int getByteSize() {
        return ConstDef.WORD_SIZE;
    }
}
