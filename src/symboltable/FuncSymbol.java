package symboltable;

import genir.IRFunction;
import genir.code.InterRepresentHolder;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class FuncSymbol {

    public Token funcName;
    public int defineOrder = -1;
    /*public IRFunction irFunction; //对应的所有IR语句*/
    public int paramNum = 0; // 因为只有整型，所以就不记录参数类型了
    public BType returnType;
    //重载标识
    public int overloadIdent = 0;
    public final List<SymbolDomain> domains = new ArrayList<>();
    public FuncSymbol(Token funcName, int defineOrder, int paramNum,int overloadIdent, BType returnType) {
        this.funcName = funcName;
        this.defineOrder = defineOrder;
        this.paramNum = paramNum;
        this.returnType = returnType;
        this.overloadIdent = overloadIdent;
    }
    public void addDomain(SymbolDomain domain)
    {
        this.domains.add(domain);
    }
    public String getAsmLabel()
    {
        return overloadIdent==0?funcName.getText():String.format("%s.%d",funcName,overloadIdent);
    }

    public boolean hasReturn()
    {
        return returnType!=BType.VOID;
    }

    public int getFrameSize()
    {
        int totalByteSize = 0;
        for (SymbolDomain domain : domains) {
            for (ValueSymbol symbol : domain.symbolTable.getAllSymbol()) {
                totalByteSize+= symbol.getByteSize();
            }
        }

        return ((int) Math.ceil(((double)totalByteSize)/4.0))*4;//4字节对齐
    }

    private boolean funcCallInside = false;

    public void setHasFuncCallInside(boolean funcCallInside) {
        this.funcCallInside = funcCallInside;
    }

    /**
     * 内部是否调用了其他函数
     */
    public boolean hasFuncCallInside()
    {
        return funcCallInside;
    }
}
