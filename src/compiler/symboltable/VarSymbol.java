package compiler.symboltable;



import compiler.genir.IRCollection;
import compiler.symboltable.initvalue.InitValue;
import org.antlr.v4.runtime.Token;

public class VarSymbol extends HasInitSymbol {
    public long offsetByte = 0;
    public BType bType = BType.INT; //好像只有int
    public boolean isFuncParam = false;
    
    public IRCollection initIR = null;
    

    public boolean hasConstInitValue = false; //如果有的话用initValues,没有的话要用InitIR计算


    public VarSymbol(long offsetByte, Token symbolToken,  InitValue initValue) {
        super(symbolToken,initValue);
        this.offsetByte = offsetByte;
    }

    public VarSymbol(long offsetByte, Token symbolToken, long[] dimensions, InitValue initValues, boolean isArray) {
        super(symbolToken,dimensions,isArray,initValues);
        this.offsetByte = offsetByte;
    }

    @Override
    public long getOffsetByte() {
        return offsetByte;
    }
}
