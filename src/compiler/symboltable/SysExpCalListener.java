package compiler.symboltable;

import antlr.SysYListener;
import antlr.SysYParser;
import compiler.Util;
import compiler.genir.code.AddressOrData;
import compiler.genir.ListenerUtil;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * 计算常数表达式的值
 */
public class SysExpCalListener implements SysYListener {

    SymbolTableHost symbolTableHost;

    public SysExpCalListener(SymbolTableHost symbolTableHost) {
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

    }

    @Override
    public void exitVarDecl(SysYParser.VarDeclContext ctx) {

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
        if(ctx.Integer_const()!=null)
        {
            ctx.result=new AddressOrData(true, Util.getIntFromStr(ctx.Integer_const().getSymbol().getText()));
        }else if(ctx.exp()!=null)
        {
            ctx.result = ctx.exp().result;
        }else if(ctx.lVal()!=null)
        {
            SysYParser.LValContext lValCtx = ctx.lVal();
            for (SysYParser.ExpContext expCtx : lValCtx.exp()) {
                if (expCtx.result==null) { //当前无法直接计算偏移量
                    return;
                }
            }
            HasInitSymbol symbol = (HasInitSymbol) symbolTableHost.searchSymbol(ctx.scope, lValCtx.Identifier().getSymbol(),
                                                                 s->s instanceof HasInitSymbol);
            if (symbol == null) {
                return;
            }

            ListenerUtil.SymbolWithOffset<HasInitSymbol> symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbol, lValCtx);
            if(symbolAndOffset==null)
            {
                System.err.println("Can't find symbol:"+ctx.lVal().Identifier().getSymbol().getText());
                return;
            }


            if(symbolAndOffset.symbol instanceof ConstSymbol
                    && symbolAndOffset.isOffsetImm())
            {
                ctx.result =
                        new AddressOrData (true,
                                           symbolAndOffset.symbol.initValues.get(symbolAndOffset.getOffsetImm()));
            }
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

    }
}
