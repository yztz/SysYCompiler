package symboltable;

import genir.IRFunction;
import genir.code.InterRepresentHolder;
import org.antlr.v4.runtime.Token;

public class FuncSymbol {

    public Token funcName;
    public int defineOrder = -1;
    public IRFunction irFunction; //对应的所有IR语句
    public int paramNum = 0; // 因为只有整型，所以就不记录参数类型了
    public BType returnType;
    //重载标识
    public int overloadIdent = 0;

    public FuncSymbol(Token funcName, int defineOrder, int paramNum,int overloadIdent, BType returnType) {
        this.funcName = funcName;
        this.defineOrder = defineOrder;
        this.paramNum = paramNum;
        this.returnType = returnType;
        this.overloadIdent = overloadIdent;
    }

    public String getAsmLabel()
    {
        return overloadIdent==0?funcName.getText():String.format("%s.%d",funcName,overloadIdent);
    }

    public boolean hasReturn()
    {
        return returnType!=BType.VOID;
    }
}
