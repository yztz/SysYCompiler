package compiler.symboltable;

import org.antlr.v4.runtime.Token;

public abstract class HasInitSymbol extends ValueSymbol{

    public int[] dimensions;
    protected int length = 0;
    protected int byteSize = 0;
    public boolean isGlobal = false;

    // 初始数据在汇编的中的标签
    public String asmDataLabel = null;

    public int[] initValues;

    public HasInitSymbol(Token symbolToken,int[] initValues) {
        super(symbolToken);
        this.initValues= initValues;
        dimensions=new int[]{1};
        length = 1;
        byteSize=4;
    }

    public HasInitSymbol(Token symbolToken, int[] dimensions, boolean isArray,int[] initValues) {
        super(symbolToken, isArray);
        this.initValues= initValues;
        this.dimensions = dimensions;
        length = 1;
        for (int dimSize : dimensions) {
            length *=dimSize;
        }
        byteSize=length*4;
    }

    public boolean isAllZero()
    {
        if(initValues==null) return true;
        for (int i = 0; i < initValues.length; i++) {
            if(initValues[i]!=0)
                return false;
        }
        return true;
    }

    public int getZeroTailLength()
    {
        if(initValues==null) return getLength();
        int length = 0;
        for (int i = initValues.length - 1; i >= 0; i--) {
            if(initValues[i]!=0)
                break;
            length++;
        }
        return length;
    }

    public boolean isGlobalSymbol()
    {
        return this.isGlobal;
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
