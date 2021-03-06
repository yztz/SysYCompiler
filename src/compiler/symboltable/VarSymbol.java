package compiler.symboltable;



import compiler.genir.IRCollection;
import compiler.symboltable.initvalue.InitValue;
import org.antlr.v4.runtime.Token;

public class VarSymbol extends HasInitSymbol {
    public int offsetByte = 0;
    public BType bType = BType.INT; //好像只有int
    public boolean isFuncParam = false;
    
    public IRCollection initIR = null;
    

    public boolean hasConstInitValue = false; //如果有的话用initValues,没有的话要用InitIR计算


    public VarSymbol(int offsetByte, Token symbolToken,  InitValue initValue) {
        super(symbolToken,initValue);
        this.offsetByte = offsetByte;
    }

    public VarSymbol(int offsetByte, Token symbolToken, int[] dimensions, InitValue initValues, boolean isArray) {
        super(symbolToken,dimensions,isArray,initValues);
        this.offsetByte = offsetByte;
    }

    @Override
    public int getOffsetByte() {
        return offsetByte;
    }
}
