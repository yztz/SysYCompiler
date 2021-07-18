package symboltable;


import com.sun.istack.internal.Nullable;
import genir.code.InterRepresent;
import org.antlr.v4.runtime.Token;

public class VarSymbol {
    public int offset = 0;
    private int length = 0;
    private int byteSize = 0;
    public Token varToken;
    public BType bType = BType.INT; //好像只有int
    public int[] dimensions;
    public boolean isFuncParam = false;
    public SymbolDomain domain;

//    public InterRepresent nextRef;
//    public boolean isAlive = true;



    @Nullable
    public int[] initValues;
    public VarSymbol(int offset, Token varToken, SymbolDomain domain) {
        this.offset = offset;
        this.varToken = varToken;
        this.domain = domain;
        dimensions=new int[]{1};
        length = 1;
    }

    public VarSymbol(int offset, Token varToken, int[] dimensions, SymbolDomain domain) {
        this.offset = offset;
        this.varToken = varToken;
        this.dimensions = dimensions;
        this.domain = domain;
        length = 1;
        for (int dimSize : dimensions) {
            length *=dimSize;
        }
        byteSize=length*4;
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
