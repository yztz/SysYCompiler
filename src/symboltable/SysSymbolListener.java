package symboltable;

import antlr.SysYListener;
import antlr.SysYParser;
import genir.code.AddressOrData;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Stack;

public class SysSymbolListener implements SysYListener {
    private Stack<SymbolDomain> blockStack = new Stack<>();
    private SymbolTable currentSymbolTable;
    private FuncSymbol currentFunc = null;
    public FuncSymbolTable funcSymbolTable;
    public SymbolTableHost symbolTableHost;

    public SysSymbolListener(SymbolTableHost symbolTableHost,FuncSymbolTable funcSymbolTable) {
        this.funcSymbolTable = funcSymbolTable;
        this.symbolTableHost = symbolTableHost;
        blockStack.push(SymbolDomain.globalDomain);
        currentSymbolTable = SymbolDomain.globalDomain.symbolTable;
    }

    @Override
    public void enterCompUnit(SysYParser.CompUnitContext ctx) {

    }

    @Override
    public void exitCompUnit(SysYParser.CompUnitContext ctx) {

    }

    @Override
    public void enterDecl(SysYParser.DeclContext ctx) {

    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx) {

    }

    @Override
    public void enterConstDecl(SysYParser.ConstDeclContext ctx) {

    }

    @Override
    public void exitConstDecl(SysYParser.ConstDeclContext ctx) {

    }

    @Override
    public void enterBType(SysYParser.BTypeContext ctx) {

    }

    @Override
    public void exitBType(SysYParser.BTypeContext ctx) {

    }

    @Override
    public void enterConstDef(SysYParser.ConstDefContext ctx) {

    }

    @Override
    public void exitConstDef(SysYParser.ConstDefContext ctx) {

    }

    @Override
    public void enterConstInitVal(SysYParser.ConstInitValContext ctx) {

    }

    @Override
    public void exitConstInitVal(SysYParser.ConstInitValContext ctx) {

    }

    @Override
    public void enterVarDecl(SysYParser.VarDeclContext ctx) {
    }

    @Override
    public void exitVarDecl(SysYParser.VarDeclContext ctx) {
        List<SysYParser.VarDefContext> varDefs = ctx.varDef();
        for (SysYParser.VarDefContext varDefCtx : varDefs) {
            TerminalNode identifier = varDefCtx.Identifier();
            if(varDefCtx.constExp()==null) //不是数组
                currentSymbolTable.addSymbol(identifier.getSymbol());
            else{ //是数组
                int[] dims= new int[varDefCtx.constExp().size()];
                for (int i = 0; i < varDefCtx.constExp().size(); i++) {
                    if (varDefCtx.constExp().get(i).result!=null&&
                            varDefCtx.constExp().get(i).result.isData) {
                        dims[i]=varDefCtx.constExp().get(i).result.item;
                    }else{
                        dims[i]=1;
                        System.err.println("Array size must be constant");
                    }
                }
                currentSymbolTable.addSymbol(identifier.getSymbol(),dims);
            }
        }
    }

    @Override
    public void enterVarDef(SysYParser.VarDefContext ctx) {

    }

    @Override
    public void exitVarDef(SysYParser.VarDefContext ctx) {

    }

    @Override
    public void enterInitVal(SysYParser.InitValContext ctx) {

    }

    @Override
    public void exitInitVal(SysYParser.InitValContext ctx) {

    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        String returnTypeStr = ctx.funcType().getText();
        BType returnType = BType.VOID;
        if(returnTypeStr.equals("int"))
        {
            returnType=BType.INT;
        }

        int paramNum = 0;
        if(ctx.funcFParams()!=null)
        {
            paramNum = ctx.funcFParams().funcFParam().size();
        }
        Token funcName = ctx.Identifier().getSymbol();

        currentFunc = funcSymbolTable.addFunc(funcName, paramNum, returnType);
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {

    }

    @Override
    public void enterFuncType(SysYParser.FuncTypeContext ctx) {

    }

    @Override
    public void exitFuncType(SysYParser.FuncTypeContext ctx) {

    }

    @Override
    public void enterFuncFParams(SysYParser.FuncFParamsContext ctx) {

    }

    @Override
    public void exitFuncFParams(SysYParser.FuncFParamsContext ctx) {

    }

    @Override
    public void enterFuncFParam(SysYParser.FuncFParamContext ctx) {

    }

    @Override
    public void exitFuncFParam(SysYParser.FuncFParamContext ctx) {

    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        SymbolDomain domain;
        if (blockStack.empty()) {
            domain = symbolTableHost.createSymbolDomain(null, currentFunc);
        }else{
            SymbolDomain topDomain = blockStack.peek();
            domain = symbolTableHost.createSymbolDomain(topDomain, currentFunc);
        }
        blockStack.push(domain);
        currentSymbolTable = domain.symbolTable;

        if(ctx.getParent() instanceof SysYParser.FuncDefContext)
        {
            SysYParser.FuncDefContext parentIfCtx = (SysYParser.FuncDefContext)ctx.getParent();
            if(parentIfCtx.funcFParams()==null)
                return;
            // 如果父节点是函数定义，那么把函数参数添加到符号表
            // 不在enterFuncDef中进行这项操作是因为新的symboltable和domain还没生成
            for (int i = 0; i < parentIfCtx.funcFParams().funcFParam().size(); i++) {
                SysYParser.FuncFParamContext paramCtx = parentIfCtx.funcFParams().funcFParam().get(i);

                TerminalNode identifier = paramCtx.Identifier();
                if(paramCtx.LeftBracket().size()==0 || paramCtx.RightBracket().size()==0) //没有[], 不是数组型参数
                    currentSymbolTable.addParam(identifier.getSymbol());
                else{ //是数组
                    int[] dims= new int[paramCtx.exp().size()+1];
                    dims[0] = -1; //-1表示未知, 第一个方括号内没有参数
                    for (int expIndex = 0; expIndex < paramCtx.exp().size(); expIndex++) {
                        if (paramCtx.exp(expIndex).result!=null&&
                                paramCtx.exp(expIndex).result.isData) {
                            dims[i+1] = paramCtx.exp(expIndex).result.item;
                        }else{
                            dims[i+1] = 1;
                            System.err.println("Array size must be constant");
                        }
                    }
                    currentSymbolTable.addParam(identifier.getSymbol(),dims);
                }
            }
        }
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        blockStack.pop();
        SymbolDomain domain = blockStack.peek();
        currentSymbolTable = domain.symbolTable;
    }

    @Override
    public void enterBlockItem(SysYParser.BlockItemContext ctx) {

    }

    @Override
    public void exitBlockItem(SysYParser.BlockItemContext ctx) {

    }

    @Override
    public void enterAssignStat(SysYParser.AssignStatContext ctx) {

    }

    @Override
    public void exitAssignStat(SysYParser.AssignStatContext ctx) {

    }

    @Override
    public void enterSemiStat(SysYParser.SemiStatContext ctx) {

    }

    @Override
    public void exitSemiStat(SysYParser.SemiStatContext ctx) {

    }

    @Override
    public void enterBlockStat(SysYParser.BlockStatContext ctx) {

    }

    @Override
    public void exitBlockStat(SysYParser.BlockStatContext ctx) {

    }

    @Override
    public void enterIfStat(SysYParser.IfStatContext ctx) {

    }

    @Override
    public void exitIfStat(SysYParser.IfStatContext ctx) {

    }

    @Override
    public void enterWhileStat(SysYParser.WhileStatContext ctx) {

    }

    @Override
    public void exitWhileStat(SysYParser.WhileStatContext ctx) {

    }

    @Override
    public void enterBreakStat(SysYParser.BreakStatContext ctx) {

    }

    @Override
    public void exitBreakStat(SysYParser.BreakStatContext ctx) {

    }

    @Override
    public void enterContinueStat(SysYParser.ContinueStatContext ctx) {

    }

    @Override
    public void exitContinueStat(SysYParser.ContinueStatContext ctx) {

    }

    @Override
    public void enterReturnStat(SysYParser.ReturnStatContext ctx) {

    }

    @Override
    public void exitReturnStat(SysYParser.ReturnStatContext ctx) {

    }

    @Override
    public void enterExp(SysYParser.ExpContext ctx) {

    }

    @Override
    public void exitExp(SysYParser.ExpContext ctx) {
        ctx.result=ctx.addExp().result;
    }

    @Override
    public void enterCond(SysYParser.CondContext ctx) {

    }

    @Override
    public void exitCond(SysYParser.CondContext ctx) {

    }

    @Override
    public void enterLVal(SysYParser.LValContext ctx) {
        boolean defined=false;
        Token symbol = ctx.Identifier().getSymbol();
        for (SymbolDomain domain : blockStack) {
            if (domain.symbolTable.containSymbol(symbol.getText())) {
                defined=true;
                break;
            }
        }

        if(!defined){
            //todo 符号未定义错误
        }
    }

    @Override
    public void exitLVal(SysYParser.LValContext ctx) {

    }

    @Override
    public void enterPrimaryExp(SysYParser.PrimaryExpContext ctx) {

    }

    @Override
    public void exitPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        if(ctx.Integer_const()!=null)
        {
            ctx.result=new AddressOrData(true, Integer.parseInt(ctx.Integer_const().getSymbol().getText()));
        }
    }

    @Override
    public void enterPrimaryExpr(SysYParser.PrimaryExprContext ctx) {

    }

    @Override
    public void exitPrimaryExpr(SysYParser.PrimaryExprContext ctx) {
        ctx.result=ctx.primaryExp().result;
    }

    @Override
    public void enterFunctionExpr(SysYParser.FunctionExprContext ctx) {

    }

    @Override
    public void exitFunctionExpr(SysYParser.FunctionExprContext ctx) {

    }

    @Override
    public void enterSignExpr(SysYParser.SignExprContext ctx) {

    }

    @Override
    public void exitSignExpr(SysYParser.SignExprContext ctx) {
        if(ctx.unaryExp().result!=null && ctx.unaryExp().result.isData)
        {
            int unaryResult = ctx.unaryExp().result.item;
            if(ctx.Plus()!=null)
            {
                ctx.result=new AddressOrData(true, unaryResult>0?unaryResult:-unaryResult);
            }else if(ctx.Minus()!=null)
            {
                ctx.result=new AddressOrData(true, unaryResult<0?unaryResult:-unaryResult);
            }
        }
    }

    @Override
    public void enterFuncRParams(SysYParser.FuncRParamsContext ctx) {

    }

    @Override
    public void exitFuncRParams(SysYParser.FuncRParamsContext ctx) {

    }

    @Override
    public void enterMulExp(SysYParser.MulExpContext ctx) {

    }

    @Override
    public void exitMulExp(SysYParser.MulExpContext ctx) {
        if(ctx.op==null)
            ctx.result=ctx.unaryExp().result;
        else if(ctx.mulExp().result!=null && ctx.unaryExp().result!=null &&
                ctx.mulExp().result.isData && ctx.unaryExp().result.isData){
            int r=0;
            if(ctx.Star()!=null)
            {
                r=ctx.mulExp().result.item *ctx.unaryExp().result.item;
            }else if(ctx.Div()!=null)
            {
                r=ctx.mulExp().result.item / ctx.unaryExp().result.item;
            }else if(ctx.Mod()!=null)
            {
                r=ctx.mulExp().result.item % ctx.unaryExp().result.item;
            }
            ctx.result=new AddressOrData(true, r);
        }
    }

    @Override
    public void enterAddExp(SysYParser.AddExpContext ctx) {

    }

    @Override
    public void exitAddExp(SysYParser.AddExpContext ctx) {
        if(ctx.op==null)
            ctx.result=ctx.mulExp().result;
        else if(ctx.mulExp().result!=null && ctx.addExp().result!=null &&
                ctx.mulExp().result.isData && ctx.addExp().result.isData){
            int r=0;
            if(ctx.Plus()!=null)
            {
                r=ctx.mulExp().result.item +ctx.addExp().result.item;
            }else if(ctx.Minus()!=null)
            {
                r=ctx.addExp().result.item - ctx.mulExp().result.item;
            }
            ctx.result=new AddressOrData(true, r);
        }
    }

    @Override
    public void enterRelExp(SysYParser.RelExpContext ctx) {

    }

    @Override
    public void exitRelExp(SysYParser.RelExpContext ctx) {

    }

    @Override
    public void enterEqExp(SysYParser.EqExpContext ctx) {

    }

    @Override
    public void exitEqExp(SysYParser.EqExpContext ctx) {

    }

    @Override
    public void enterLAndExp(SysYParser.LAndExpContext ctx) {

    }

    @Override
    public void exitLAndExp(SysYParser.LAndExpContext ctx) {

    }

    @Override
    public void enterLOrExp(SysYParser.LOrExpContext ctx) {

    }

    @Override
    public void exitLOrExp(SysYParser.LOrExpContext ctx) {

    }

    @Override
    public void enterConstExp(SysYParser.ConstExpContext ctx) {

    }

    @Override
    public void exitConstExp(SysYParser.ConstExpContext ctx) {
        ctx.result = ctx.addExp().result;
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
        if(parserRuleContext instanceof SysYParser.DomainedContext)
        {
            ((SysYParser.DomainedContext)parserRuleContext).domain =blockStack.peek();
        }
    }
}
