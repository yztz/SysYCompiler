package symboltable;

import org.antlr.v4.runtime.Token;

public class ValueSymbol {
    public Token symbolToken;
    public int[] dimensions;
    protected int length = 0;
    protected int byteSize = 0;

    public ValueSymbol(Token symbolToken) {
        this.symbolToken = symbolToken;
        dimensions=new int[]{1};
        length = 1;
    }

    public ValueSymbol(Token symbolToken, int[] dimensions) {
        this.symbolToken = symbolToken;
        this.dimensions = dimensions;
        length = 1;
        for (int dimSize : dimensions) {
            length *=dimSize;
        }
        byteSize=length*4;
    }

    /**
     * 展开后的一维长度
     */
    public int getLength() {
        return length;
    }

    /**
     * 总共占用多少字节
     */
    public int getByteSize() {
        return byteSize;
    }
}
