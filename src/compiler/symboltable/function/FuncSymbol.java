package compiler.symboltable.function;

import compiler.Location;
import compiler.symboltable.BType;
import compiler.symboltable.ParamSymbol;
import compiler.symboltable.SymbolDomain;
import compiler.symboltable.ValueSymbol;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class FuncSymbol extends AbstractFuncSymbol{

    private final Token funcNameToken;
    public int defineOrder = -1;
    public Location defineLocation;
    /*public IRFunction irFunction; //对应的所有IR语句*/

    public BType returnType;
    //重载标识
    public int overloadIdent = 0;

    public int totalSymbolOffset = 0;
    public final List<SymbolDomain> domains = new ArrayList<>();
    public final List<ParamSymbol> paramSymbols = new ArrayList<>();
    public int getParamNum()
    {
        return paramSymbols.size();
    }
    public FuncSymbol(Token funcNameToken, int defineOrder, int overloadIdent,Location defineLoc, BType returnType) {
        this.funcNameToken = funcNameToken;
        this.defineOrder = defineOrder;
        this.returnType = returnType;
        this.overloadIdent = overloadIdent;
        this.defineLocation = defineLoc;
    }
    public void addDomain(SymbolDomain domain)
    {
        this.domains.add(domain);
    }
    public String getFuncName()
    {
        return funcNameToken.getText();
    }
    public String getAsmLabel()
    {
        return overloadIdent==0? funcNameToken.getText():String.format("%s.%d", funcNameToken, overloadIdent);
    }
    public String getAsmEndLabel()
    {
        return String.format("%s.end",getAsmLabel());
    }
    public boolean hasReturn()
    {
        return returnType!=BType.VOID;
    }

    private int stackFrameSize;
    public void setStackFrameSize(int val)
    {
        stackFrameSize =val;
    }
    public int getStackFrameSize()
    {
        return stackFrameSize;
    }

    private boolean funcCallInside = false;

    public void setHasFuncCallInside(boolean funcCallInside) {
        this.funcCallInside = funcCallInside;
    }

    /**
     * 内部是否调用了其他函数
     * 调用了其他函数，汇编生成时，需要将lr入栈
     * (有可能IR中没有调用函数，但是汇编生成时添加了对函数的调用，例如memcpy,所以通过此方法判断并不准确)
     */
    public boolean hasFuncCallInside()
    {
        return funcCallInside;
    }
}
