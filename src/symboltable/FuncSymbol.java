package symboltable;

import genir.code.InterRepresentHolder;
import org.antlr.v4.runtime.Token;

public class FuncSymbol {

    public Token funcName;
    public int defineOrder = -1;
    public InterRepresentHolder firstStmtHolder; //第一条语句的位置
    public int paramNum = 0; // 因为只有整型，所以就不记录参数类型了
    public BType returnType;

    public FuncSymbol(Token funcName, int defineOrder, int paramNum, BType returnType) {
        this.funcName = funcName;
        this.defineOrder = defineOrder;
        this.paramNum = paramNum;
        this.returnType = returnType;
    }

    public boolean hasReturn()
    {
        return returnType!=BType.VOID;
    }
}
