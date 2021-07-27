package compiler.symboltable;



import compiler.genir.IRGroup;
import org.antlr.v4.runtime.Token;

public class VarSymbol extends ValueSymbol {
    public int offsetByte = 0;
    public BType bType = BType.INT; //好像只有int
    public boolean isFuncParam = false;
    
    public IRGroup initIR = null;
    
    public int[] initValues;
    public boolean hasConstInitValue = false; //如果有的话用initValues,没有的话要用InitIR计算

    // 初始数据在汇编的中的标签
    public String asmDataLabel = null;

    public VarSymbol(int offsetByte, Token symbolToken,  int[] initValues) {
        super(symbolToken);
        this.offsetByte = offsetByte;
        this.initValues = initValues;
    }

    public VarSymbol(int offsetByte, Token symbolToken, int[] dimensions,  int[] initValues,boolean isArray) {
        super(symbolToken,dimensions,isArray);
        this.offsetByte = offsetByte;
        this.initValues = initValues;
    }

    @Override
    public int getOffsetByte() {
        return offsetByte;
    }
}
