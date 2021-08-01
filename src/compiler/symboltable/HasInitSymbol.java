package compiler.symboltable;

import compiler.symboltable.initvalue.InitValue;
import compiler.symboltable.initvalue.SingleInitValue;
import org.antlr.v4.runtime.Token;

public abstract class HasInitSymbol extends ValueSymbol{

    public long[] dimensions;
    protected long length = 0;
    protected long byteSize = 0;
    public boolean isGlobal = false;

    // 初始数据在汇编的中的标签
    public String asmDataLabel = null;

    public InitValue initValues;

    public HasInitSymbol(Token symbolToken,InitValue initVal) {
        super(symbolToken);
        this.initValues= initVal;
        dimensions=new long[]{1};
        length = 1;
        byteSize=4;
    }

    public HasInitSymbol(Token symbolToken, long[] dimensions, boolean isArray, InitValue initValues) {
        super(symbolToken, isArray);
        this.initValues= initValues;
        this.dimensions = dimensions;
        length = 1;
        for (long dimSize : dimensions) {
            length *=dimSize;
        }
        byteSize=length*4;
    }

    public boolean isAllZero()
    {
        return initValues.isAllZero();
    }

    public long getZeroTailLength()
    {
        if(initValues==null) return getLength();

        return getLength() - initValues.getLastNonZeroPos()-1;
    }

    public boolean isGlobalSymbol()
    {
        return this.isGlobal;
    }

    /**
     * 展开后的一维长度
     */
    public long getLength() {
        return length;
    }

    /**
     * 总共占用多少字节
     */
    public long getByteSize() {
        return byteSize;
    }

}
