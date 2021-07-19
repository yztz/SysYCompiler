package symboltable;


import com.sun.istack.internal.Nullable;
import genir.code.InterRepresent;
import org.antlr.v4.runtime.Token;

public class VarSymbol extends ValueSymbol {
    public int offset = 0;
    public BType bType = BType.INT; //好像只有int
    public boolean isFuncParam = false;


//    public InterRepresent nextRef;
//    public boolean isAlive = true;



    @Nullable
    public int[] initValues;
    public VarSymbol(int offset, Token symbolToken,@Nullable int[] initValues, SymbolDomain domain) {
        super(symbolToken, domain);
        this.offset = offset;
        this.initValues = initValues;
    }

    public VarSymbol(int offset, Token symbolToken, int[] dimensions,@Nullable int[] initValues, SymbolDomain domain) {
        super(symbolToken,dimensions, domain);
        this.offset = offset;
        this.initValues = initValues;
    }
}
