package symboltable;


import org.antlr.v4.runtime.Token;

public class VarSymbol {
    public int offset=0;
    public Token varToken;
    public BType bType = BType.INT; //好像只有int
    public int[] dimensions;
    public boolean isFuncParam = false;
    public VarSymbol(int offset, Token varToken) {
        this.offset = offset;
        this.varToken = varToken;
        dimensions=new int[]{1};
    }

    public VarSymbol(int offset, Token varToken, int[] dimensions) {
        this.offset = offset;
        this.varToken = varToken;
        this.dimensions = dimensions;
    }
}
