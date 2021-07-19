package symboltable;


import com.sun.istack.internal.Nullable;
import org.antlr.v4.runtime.Token;

public class VarSymbol extends ValueSymbol {
    public int offset = 0;
    public BType bType = BType.INT; //好像只有int
    public boolean isFuncParam = false;
    @Nullable
    public int[] initValues;
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
