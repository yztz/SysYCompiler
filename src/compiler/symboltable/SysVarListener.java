package compiler.symboltable;

import antlr.SysYListener;
import antlr.SysYParser;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.ListenerUtil;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SysVarListener implements SysYListener {


    public FuncSymbolTable funcSymbolTable;
    public SymbolTableHost symbolTableHost;

    public SysVarListener(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        this.funcSymbolTable = funcSymbolTable;
        this.symbolTableHost = symbolTableHost;
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
        List<SysYParser.VarDefContext> varDefs = ctx.varDef();
        for (SysYParser.VarDefContext varDefCtx : varDefs) {
            TerminalNode identifier = varDefCtx.Identifier();

            //没有初始值，不要执行下面的
            if(varDefCtx.initVal()==null) continue;

            if (varDefCtx.constExp() == null) //不是数组
            {
                varDefCtx.initVal().dimensions = new int[]{1};
                varDefCtx.initVal().initValues = new int[1];
            }
            else{ //是数组
                int[] dims = getDimsFromConstExp(varDefCtx.constExp());
                varDefCtx.initVal().dimensions = dims;
                int length = getLengthFromDimensions(dims);
                varDefCtx.initVal().initValues = new int[length];
            }
            varDefCtx.initVal().ident= identifier.getSymbol();
            varDefCtx.initVal().symbolOffset=0;
            varDefCtx.initVal().whichDim = 1;
        }
    }

    @Override
    public void exitVarDecl(SysYParser.VarDeclContext ctx) {
        SymbolTable currentSymbolTable = ctx.domain.symbolTable;
        List<SysYParser.VarDefContext> varDefs = ctx.varDef();
        for (SysYParser.VarDefContext varDefCtx : varDefs) {
            TerminalNode identifier = varDefCtx.Identifier();

            int[] initValues = null;
            int[] dimensions = null;
            if(varDefCtx.initVal()!=null)
            {
                initValues = varDefCtx.initVal().initValues;
            }
            VarSymbol varSymbol;
            if(varDefCtx.constExp()==null || varDefCtx.constExp().size()==0) //不是数组
            {
                // 遍历完成，记录数据
                varSymbol = currentSymbolTable.addVar(identifier.getSymbol(), initValues);
            }else{
                int[] dims = getDimsFromConstExp(varDefCtx.constExp());
                // 遍历完成，记录数据
                varSymbol = currentSymbolTable.addVarArray(identifier.getSymbol(), dims, initValues);
            }
            if (currentSymbolTable.getDomain()== SymbolDomain.globalDomain) {
                varSymbol.isGlobal = true;
            }
            if(varDefCtx.initVal()!=null)
            {
                varSymbol.hasConstInitValue = varDefCtx.initVal().hasConstInitValue;
            }
        }
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

        int dimSize = 1;
        for(int i=ctx.whichDim;i<ctx.dimensions.length;i++)
        {
            dimSize*=ctx.dimensions[i];
        }
        int symbolOffset = ctx.symbolOffset;
        for (int i = 0; i < ctx.initVal().size(); i++) {
            SysYParser.InitValContext childInitVal = ctx.initVal().get(i);
            childInitVal.ident = ctx.ident;
            childInitVal.dimensions = ctx.dimensions;
            childInitVal.initValues = ctx.initValues;
            childInitVal.whichDim = ctx.whichDim + 1;
            childInitVal.symbolOffset=symbolOffset;
            if(childInitVal.exp()!=null)
            {
                symbolOffset+=1;
            }else{
                symbolOffset+=dimSize;
            }
        }
    }

    @Override
    public void exitInitVal(SysYParser.InitValContext ctx) {
        boolean hasConstInitVal = true;
        if(ctx.exp()!=null)
        {
            AddressOrData initResult = ctx.exp().result;
            if (initResult!=null && initResult.isData) {
                ctx.initValues[ctx.symbolOffset]=initResult.item;
            }else{
                hasConstInitVal = false;
            }
        }/*else{
            hasConstInitVal=false;
        }*/
        // 必须每一项都有常数值
        for (SysYParser.InitValContext initValCtx : ctx.initVal()) {
            hasConstInitVal&=initValCtx.hasConstInitValue;
        }
        ctx.hasConstInitValue = hasConstInitVal;
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {

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

    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {

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

    }
}
