package symboltable;


import com.sun.istack.internal.Nullable;
import genir.IRGroup;
import org.antlr.v4.runtime.Token;

public class VarSymbol extends ValueSymbol {
    public int offset = 0;
    public BType bType = BType.INT; //好像只有int
    public boolean isFuncParam = false;
    @Nullable
    public IRGroup initIR = null;
    @Nullable
    public int[] initValues;
    public boolean hasConstInitValue = false; //如果有的话用initValues,没有的话要用InitIR计算

    // 初始数据在汇编的中的标签
    public String asmDataLabel = null;

    public VarSymbol(int offset, Token symbolToken,@Nullable int[] initValues) {
        super(symbolToken);
        this.offset = offset;
        this.initValues = initValues;
    }

    public VarSymbol(int offset, Token symbolToken, int[] dimensions,@Nullable int[] initValues) {
        super(symbolToken,dimensions);
        this.offset = offset;
        this.initValues = initValues;
    }
}
