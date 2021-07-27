package compiler.symboltable;

import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public abstract class ValueSymbol {
    public Token symbolToken;
    public int[] dimensions;
    protected int length = 0;
    protected int byteSize = 0;
    public boolean isGlobal = false;

    private Map<FuncSymbol,Integer> indexInFuncDataMap = new HashMap<>();
    public ValueSymbol(Token symbolToken) {
        this.symbolToken = symbolToken;
        dimensions=new int[]{1};
        length = 1;
    }

    public ValueSymbol(Token symbolToken, int[] dimensions,boolean isArray) {
        this.symbolToken = symbolToken;
        this.dimensions = dimensions;
        length = 1;
        for (int dimSize : dimensions) {
            length *=dimSize;
        }
        byteSize=length*4;
        this.isArray = isArray;
    }

    public void setIndexInFunctionData(int index,FuncSymbol funcSymbol)
    {
        indexInFuncDataMap.put(funcSymbol,index);
    }

    public int getIndexInFunctionData(FuncSymbol funcSymbol)
    {
        return indexInFuncDataMap.get(funcSymbol);
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

    private boolean isArray = false;
    public boolean isArray(){
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public abstract int getOffsetByte();

    public boolean isGlobalSymbol()
    {
        return this.isGlobal;
    }
}
