package compiler.symboltable;

import antlr.SysYListener;
import antlr.SysYParser;
import compiler.genir.code.AddressOrData;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Stack;

public class SysFuncConstListener implements SysYListener {
    private Stack<SymbolDomain> blockStack = new Stack<>();
    private SymbolTable currentSymbolTable;
    private FuncSymbol currentFunc = null;
    public FuncSymbolTable funcSymbolTable;
    public SymbolTableHost symbolTableHost;

    public SysFuncConstListener(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
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
        for (SysYParser.ConstDefContext defCtx : ctx.constDef()) {
            TerminalNode identifier = defCtx.Identifier();
            if(defCtx.constExp()==null || defCtx.constExp().isEmpty()) //不是数组
            {
                defCtx.constInitVal().initValues = new int[1];
                defCtx.constInitVal().dimensions= new int[]{1};
            }
            else{ //是数组
                defCtx.constInitVal().dimensions = getDimsFromConstExp(defCtx.constExp());
                int length = getLengthFromDimensions(defCtx.constInitVal().dimensions);
                defCtx.constInitVal().initValues = new int[length];
            }
            defCtx.constInitVal().whichDim = 1;
            defCtx.constInitVal().ident = identifier.getSymbol();
            defCtx.constInitVal().symbolOffset=0;
        }
    }

    @Override
    public void exitConstDecl(SysYParser.ConstDeclContext ctx) {
        for (SysYParser.ConstDefContext defCtx : ctx.constDef()) {
            TerminalNode identifier = defCtx.Identifier();
            if(defCtx.constExp()==null) //不是数组
            {
                currentSymbolTable.addConst(identifier.getSymbol(),defCtx.constInitVal().initValues[0]);
            }
            else{ //是数组
                currentSymbolTable.addConstArray(identifier.getSymbol(),defCtx.constInitVal().dimensions,
                                            defCtx.constInitVal().initValues);
            }
        }
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
        int dimSize = 1;
        for(int i=ctx.whichDim;i<ctx.dimensions.length;i++)
        {
            dimSize*=ctx.dimensions[i];
        }
        int symbolOffset = ctx.symbolOffset;
        for (int i = 0; i < ctx.constInitVal().size(); i++) {
            SysYParser.ConstInitValContext childInitVal = ctx.constInitVal().get(i);
            childInitVal.ident = ctx.ident;
            childInitVal.dimensions = ctx.dimensions;
            childInitVal.initValues = ctx.initValues;
            childInitVal.whichDim = ctx.whichDim + 1;
            childInitVal.symbolOffset=symbolOffset;
            if(childInitVal.constExp()!=null)
            {
                symbolOffset+=1;
            }else{
                symbolOffset+=dimSize;
            }
        }
    }

    @Override
    public void exitConstInitVal(SysYParser.ConstInitValContext ctx) {
        if(ctx.constExp()!=null)
        {
            AddressOrData initResult = ctx.constExp().result;
            if (initResult.isData) {
                ctx.initValues[ctx.symbolOffset]=initResult.item;
            }else{

                //todo 这些咋办
                /*VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,ctx.ident);
                SaveRepresent ir = InterRepresentFactory.createSaveRepresent(ctx.symbol, new AddressOrData(true, ctx.symbolOffset),
                                                                             initResult);
                irCodes.addCode(ir);*/
            }
        }
    }

    @Override
    public void enterVarDecl(SysYParser.VarDeclContext ctx) {

    }

    @Override
    public void exitVarDecl(SysYParser.VarDeclContext ctx) {

    }
    private int getLengthFromDimensions(int[] dimensions)
    {
        int length = 1;
        for (int dim : dimensions) {
            length*=dim;
        }
        return length;
    }
    private int[] getDimsFromConstExp(List<SysYParser.ConstExpContext> expCtxList) {
        int[] dims= new int[expCtxList.size()];
        for (int i = 0; i < expCtxList.size(); i++) {
            if (expCtxList.get(i).result!=null&&
                    expCtxList.get(i).result.isData) {
                dims[i]= expCtxList.get(i).result.item;
            }else{
                dims[i]=1;
                System.err.println("Array size must be constant");
            }
        }
        return dims;
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
                            dims[i+1] = -1;
                            System.err.println("Array size must be constant");
                        }
                    }
                    currentSymbolTable.addParamArray(identifier.getSymbol(), dims);
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
        ctx.result = ctx.addExp().result;
    }

    @Override
    public void enterCond(SysYParser.CondContext ctx) {

    }

    @Override
    public void exitCond(SysYParser.CondContext ctx) {

    }

    @Override
    public void enterLVal(SysYParser.LValContext ctx) {

    }

    @Override
    public void exitLVal(SysYParser.LValContext ctx) {

    }

    @Override
    public void enterPrimaryExp(SysYParser.PrimaryExpContext ctx) {

    }

    @Override
    public void exitPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        /*if(ctx.lVal()!=null)
        {
            SysYParser.LValContext lValCtx = ctx.lVal();
            for (SysYParser.ExpContext expCtx : lValCtx.exp()) {
                if (expCtx.result==null) { //当前无法直接计算偏移量
                    return;
                }
            }
            ListenerUtil.SymbolWithOffset symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbolTableHost, lValCtx);
            if(symbolAndOffset==null)
            {
                System.err.println("Can't find symbol:"+ctx.lVal().Identifier().getSymbol().getText());
                return;
            }


            if(symbolAndOffset.symbol instanceof ConstSymbol
                    && symbolAndOffset.offsetResult.isData)
            {
                ctx.result =
                        new AddressOrData (true,
                                           ((ConstSymbol) symbolAndOffset.symbol).constVal[symbolAndOffset.offsetResult.item]);
            }
        }*/
    }

    @Override
    public void enterPrimaryExpr(SysYParser.PrimaryExprContext ctx) {

    }

    @Override
    public void exitPrimaryExpr(SysYParser.PrimaryExprContext ctx) {
        ctx.result = ctx.primaryExp().result;
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
    }

    @Override
    public void enterAddExp(SysYParser.AddExpContext ctx) {

    }

    @Override
    public void exitAddExp(SysYParser.AddExpContext ctx) {
        if(ctx.op==null)
            ctx.result = ctx.mulExp().result;
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
