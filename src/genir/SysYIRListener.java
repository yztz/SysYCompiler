package genir;

import antlr.SysYListener;
import antlr.SysYParser;
import genir.code.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import symboltable.FuncSymbol;
import symboltable.FuncSymbolTable;
import symboltable.SymbolTableHost;
import symboltable.VarSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * 深度优先遍历语法树
 * 将语法树翻译为中间代码
 */
public class SysYIRListener implements SysYListener {
    public SymbolTableHost symbolTableHost;
    public FuncSymbolTable funcSymbolTable;

    public SysYIRListener(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        this.symbolTableHost = symbolTableHost;
        this.funcSymbolTable = funcSymbolTable;
    }

    public IRCode irCodes=new IRCode();

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
        TerminalNode identifier = ctx.Identifier();
        FuncSymbol funcSymbol = funcSymbolTable.getFuncSymbol(identifier.getSymbol().getText());
        InterRepresentHolder startStmtHolder = ctx.block().startStmtHolder;
        if(startStmtHolder==null)//一条语句也没有
        {
            ReturnRepresent returnIR = new ReturnRepresent();
            funcSymbol.firstStmtHolder=new InterRepresentHolder(returnIR);
            irCodes.addCode(returnIR);
        }else{
            funcSymbol.firstStmtHolder=startStmtHolder;
            irCodes.addCode(new ReturnRepresent()); //先加上，如果多余了，优化的时候再去掉
        }
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
        List<InterRepresentHolder> breakQuads=new ArrayList<>();
        List<InterRepresentHolder> continueQuads=new ArrayList<>();
        for (SysYParser.BlockItemContext blockItemContext : ctx.blockItem()) {
            if(blockItemContext.getBreakQuads()!=null)
                breakQuads.addAll(blockItemContext.getBreakQuads());
            if(blockItemContext.getContinueQuads()!=null)
                continueQuads.addAll(blockItemContext.getContinueQuads());
        }

        ctx.setBreakQuads(breakQuads);
        ctx.setContinueQuads(continueQuads);

        // 找到第一条IR语句
        for (SysYParser.BlockItemContext blockItemContext : ctx.blockItem()) {
            if (blockItemContext.startStmtHolder!=null) {
                ctx.startStmtHolder=blockItemContext.startStmtHolder;
                break;
            }
        }
    }

    @Override
    public void enterBlockItem(SysYParser.BlockItemContext ctx) {

    }

    @Override
    public void exitBlockItem(SysYParser.BlockItemContext ctx) {
        if(ctx.stmt()!=null)
        {
            ctx.setBreakQuads(ctx.stmt().getBreakQuads());
            ctx.setContinueQuads(ctx.stmt().getContinueQuads());
            ctx.startStmtHolder=ctx.stmt().startStmtHolder;
        }
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
        ctx.setBreakQuads(ctx.block().getBreakQuads());
        ctx.setContinueQuads(ctx.block().getContinueQuads());
    }

    @Override
    public void enterIfStat(SysYParser.IfStatContext ctx) {

    }

    @Override
    public void exitIfStat(SysYParser.IfStatContext ctx) {
        if(ctx.stmt().size()==2) //有else
        {
            InterRepresent elseStartIr=ctx.stmt(1).startStmtHolder.getInterRepresent();
            for (GotoRepresent ir : ctx.cond().falseList) {
                ir.setTargetIR(elseStartIr);
            }
            // 需要插入一句goto，在if的代码块执行完成后跳过else代码块
            GotoRepresent skipElseStmtIR = new GotoRepresent(null);
            irCodes.insertCode(elseStartIr.lineNum,skipElseStmtIR);
            irCodes.bookVacancy(skipElseStmtIR.targetHolder);
        }else{
            for (GotoRepresent ir : ctx.cond().falseList) {
                irCodes.bookVacancy(ir.targetHolder);
            }
        }

        // 传递breakQuad和continueQuad给父级可能存在的while节点
        if(ctx.stmt().size()==2) //有else
        {
            ctx.setBreakQuads(mergeList(ctx.stmt(0).getBreakQuads(),ctx.stmt(1).getBreakQuads()));
            ctx.setContinueQuads(mergeList(ctx.stmt(0).getContinueQuads(),ctx.stmt(1).getContinueQuads()));
        }else{
            ctx.setBreakQuads(ctx.stmt(0).getBreakQuads());
            ctx.setContinueQuads(ctx.stmt(0).getContinueQuads());
        }
    }

    @Override
    public void enterWhileStat(SysYParser.WhileStatContext ctx) {

    }

    @Override
    public void exitWhileStat(SysYParser.WhileStatContext ctx) {
        InterRepresent whileStartIR = ctx.startStmtHolder.getInterRepresent();
        GotoRepresent returnIR = new GotoRepresent(whileStartIR);
        irCodes.addCode(returnIR);

        InterRepresent stmtStartIR = ctx.stmt().startStmtHolder.getInterRepresent();
        for (GotoRepresent ir : ctx.cond().trueList) {
            ir.targetHolder.setInterRepresent(stmtStartIR);
        }
        for (GotoRepresent ir : ctx.cond().falseList) {
            irCodes.bookVacancy(ir.targetHolder);
        }

        if (ctx.stmt().getBreakQuads() != null) {
            for (InterRepresentHolder breakQuad : ctx.stmt().getBreakQuads()) {
                irCodes.bookVacancy(breakQuad);
            }
        }

        if(ctx.stmt().getContinueQuads()!=null)
        {
            for (InterRepresentHolder continueQuad : ctx.stmt().getContinueQuads()) {
                continueQuad.setInterRepresent(whileStartIR);
            }
        }
    }

    @Override
    public void enterBreakStat(SysYParser.BreakStatContext ctx) {

    }

    @Override
    public void exitBreakStat(SysYParser.BreakStatContext ctx) {
        GotoRepresent breakGoto=new GotoRepresent(null);
        ctx.setBreakQuads(new ArrayList<>());
        ctx.getBreakQuads().add(breakGoto.targetHolder);
        irCodes.addCode(breakGoto);
    }

    @Override
    public void enterContinueStat(SysYParser.ContinueStatContext ctx) {

    }

    @Override
    public void exitContinueStat(SysYParser.ContinueStatContext ctx) {
        GotoRepresent continueGoto=new GotoRepresent(null);
        ctx.setContinueQuads(new ArrayList<>());
        ctx.getContinueQuads().add(continueGoto.targetHolder);
        irCodes.addCode(continueGoto);
    }

    @Override
    public void enterReturnStat(SysYParser.ReturnStatContext ctx) {

    }

    @Override
    public void exitReturnStat(SysYParser.ReturnStatContext ctx) {
        ReturnRepresent returnRepresent;
        if (ctx != null) {
            returnRepresent =new ReturnRepresent(ctx.exp().result);
        }else{
            returnRepresent =new ReturnRepresent();
        }
        irCodes.addCode(returnRepresent);
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
        ctx.trueList=ctx.lOrExp().trueList;
        ctx.falseList=ctx.lOrExp().falseList;
        for (GotoRepresent ir : ctx.trueList) {
            irCodes.bookVacancy(ir.targetHolder);
            //System.out.println("true "+ir.lineNum);
        }
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
        TerminalNode numTerminal = ctx.Integer_const();
        if(numTerminal!=null)
        {
            ctx.result = new AddressOrData(true, Integer.parseInt(numTerminal.getSymbol().getText()));
        }else if(ctx.lVal()!=null) //左值，变量
        {
            SysYParser.LValContext lValCtx = ctx.lVal();
            Token token = lValCtx.Identifier().getSymbol();
            VarSymbol varSymbol = symbolTableHost.searchSymbol(ctx.domain, token);
            if(varSymbol==null){
                System.err.println("Symbol is not defined");
            }else{
                int[] dimensions = varSymbol.dimensions;
                int[] dimSizes = new int[dimensions.length];
                for (int i = 0; i < dimensions.length; i++) {
                    dimSizes[i]=1;
                    for(int j = i+1;j<dimensions.length;j++)
                    {
                        dimSizes[i]*=dimensions[j];
                    }
                }

                int offsetConst = 0 ;
                AddressOrData lastResult = null;

                //数组的处理
                for (int i = 0; i < lValCtx.exp().size(); i++) {
                    AddressOrData indexInDim = lValCtx.exp(i).result;
                    if(!indexInDim.isData) //是变量
                    {
                        // 计算当前维度偏移了多少
                        BinocularRepre irMul = InterRepresentFactory.createBinocularRepresent(
                                BinocularRepre.Opcodes.MUL, new AddressOrData(true, dimSizes[i]), indexInDim);
                        irCodes.addCode(irMul);
                        if(lastResult!=null)
                        {
                            BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(
                                    BinocularRepre.Opcodes.ADD, lastResult, irMul.target);
                            irCodes.addCode(irAdd);
                            lastResult = irAdd.target;
                        }else{
                            lastResult = irMul.target;
                        }
                    }else{ //是常量
                        offsetConst+=dimSizes[i]* indexInDim.item;
                    }
                }
                AddressOrData offset;
                if(lastResult==null) // 全都是常量的情况下
                {
                    offset = new AddressOrData(true,offsetConst);
                }else if(offsetConst==0) //全是变量的情况下
                {
                    offset=lastResult;
                }else{ //变量和常量混合
                    BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(
                            BinocularRepre.Opcodes.ADD, lastResult, new AddressOrData(true,offsetConst));
                    irCodes.addCode(irAdd);
                    offset=irAdd.target;
                }
                LoadRepresent loadRepresent = InterRepresentFactory.createLoadRepresent(varSymbol, offset);
                irCodes.addCode(loadRepresent);
                ctx.result = loadRepresent.target;
            }

        }else{
            ctx.result=ctx.exp().result;
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
        TerminalNode identifier = ctx.Identifier();
        FuncSymbol funcSymbol=funcSymbolTable.getFuncSymbol(identifier.getSymbol().getText());
        if(funcSymbol==null)
            System.out.println("Function is not defined");

        CallRepresent ir;
        if (ctx.funcRParams() != null) {
            List<SysYParser.ExpContext> expContextList = ctx.funcRParams().exp();
            AddressOrData[] params = new AddressOrData[expContextList.size()];
            for (int i = 0; i < expContextList.size(); i++) {
                params[i]=expContextList.get(i).result;
            }
            ir = InterRepresentFactory.createFuncCallRepresent(funcSymbol,params);
        }else{
            ir = InterRepresentFactory.createFuncCallRepresent(funcSymbol);
        }
        irCodes.addCode(ir);
        ctx.result = ir.returnResult;
    }

    @Override
    public void enterSignExpr(SysYParser.SignExprContext ctx) {

    }

    @Override
    public void exitSignExpr(SysYParser.SignExprContext ctx) {

        UnaryRepre.UnaryOp opcode = null;
        if(ctx.Plus()!=null)
        {
            opcode = UnaryRepre.UnaryOp.ADD;
        }else if(ctx.Minus()!=null)
        {
            opcode = UnaryRepre.UnaryOp.MINUS;
        }else if(ctx.Not()!=null)
        {
            opcode = UnaryRepre.UnaryOp.NOT;
        }

        UnaryRepre ir = InterRepresentFactory.createUnaryRepresent(opcode, ctx.unaryExp().result);
        irCodes.addCode(ir);
        ctx.result=ir.target;
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
        if (ctx.op==null) {
            ctx.result=ctx.unaryExp().result;
        }else{
            BinocularRepre.Opcodes opcodes=null;
            if (ctx.Star() != null) {
                opcodes= BinocularRepre.Opcodes.MUL;
            }else if(ctx.Div()!=null)
            {
                opcodes= BinocularRepre.Opcodes.DIV;
            }else if(ctx.Mod()!=null){
                opcodes= BinocularRepre.Opcodes.MOD;
            }else{
                System.err.println("Unknown opcodes");
            }
            BinocularRepre ir= InterRepresentFactory.createBinocularRepresent(opcodes,ctx.mulExp().result,
                                                                              ctx.unaryExp().result);
            irCodes.addCode(ir);
            ctx.result = ir.target;
        }
    }

    @Override
    public void enterAddExp(SysYParser.AddExpContext ctx) {

    }

    @Override
    public void exitAddExp(SysYParser.AddExpContext ctx) {
        if (ctx.op==null) {
            ctx.result=ctx.mulExp().result;
        }else{
            BinocularRepre.Opcodes opcodes=null;
            if (ctx.Plus() != null) {
                opcodes= BinocularRepre.Opcodes.ADD;
            }else if(ctx.Minus()!=null)
            {
                opcodes= BinocularRepre.Opcodes.MINUS;
            }else{
                System.err.println("Unknown opcodes");
            }
            BinocularRepre ir= InterRepresentFactory.createBinocularRepresent(opcodes, ctx.addExp().result,
                                                                              ctx.mulExp().result);
            irCodes.addCode(ir);
            ctx.result = ir.target;
        }
    }

    private <T> List<T> mergeList(List<T> from1,
                           List<T> from2)
    {
        List<T> result = null;
        if(from1!=null || from2!=null)
            result=new ArrayList<>();

        if(from1!=null)
            result.addAll(from1);
        if(from2!=null)
            result.addAll(from2);
        return result;
    }

    @Override
    public void enterRelExp(SysYParser.RelExpContext ctx) {
        ctx.quad = irCodes.getNextLineNum();
    }

    private void createIfGotoPair(SysYParser.BranchContextBase ctx, IfGotoRepresent.RelOp relOp,
                                  AddressOrData address1, AddressOrData address2) {
        IfGotoRepresent ifGoto = new IfGotoRepresent(null, relOp, address1, address2);
        GotoRepresent goTo = new GotoRepresent(null);
        ctx.trueList=new ArrayList<>();
        ctx.falseList=new ArrayList<>();
        ctx.trueList.add(ifGoto);
        ctx.falseList.add(goTo);
        irCodes.addCode(ifGoto);
        irCodes.addCode(goTo);
    }
    @Override
    public void exitRelExp(SysYParser.RelExpContext ctx) {
        if (ctx.op==null) {
            ctx.address=ctx.addExp().result;
        }else
        {
            IfGotoRepresent.RelOp relOp= null;
            if(ctx.Greater()!=null){
                relOp= IfGotoRepresent.RelOp.GREATER;
            }else if(ctx.Less()!=null){
                relOp= IfGotoRepresent.RelOp.LESS;
            }else if(ctx.GreaterEqual()!=null){
                relOp= IfGotoRepresent.RelOp.GREATER_EQUAL;
            }else if(ctx.LessEqual()!=null){
                relOp= IfGotoRepresent.RelOp.LESS_EQUAL;
            }else{
                System.err.println("Unknown rel opcode");
            }

            createIfGotoPair(ctx, relOp,ctx.relExp().address,ctx.addExp().result);
        }
    }

    @Override
    public void enterEqExp(SysYParser.EqExpContext ctx) {

    }

    @Override
    public void exitEqExp(SysYParser.EqExpContext ctx) {
        if(ctx.op==null)
        {
            ctx.quad=ctx.relExp().quad;
            ctx.trueList=ctx.relExp().trueList;
            ctx.falseList=ctx.relExp().falseList;
            ctx.address=ctx.relExp().address;
        }else{
            ctx.quad= irCodes.getNextLineNum();
            IfGotoRepresent.RelOp relOp= null;
            if(ctx.Equal()!=null){
                relOp= IfGotoRepresent.RelOp.EQUAL;
            }else if(ctx.NotEqual()!=null){
                relOp= IfGotoRepresent.RelOp.NOT_EQUAL;
            }else{
                System.err.println("Unknown rel opcode");
            }


            createIfGotoPair(ctx, relOp,ctx.eqExp().address,ctx.relExp().address);
        }
    }



    @Override
    public void enterLAndExp(SysYParser.LAndExpContext ctx) {

    }

    @Override
    public void exitLAndExp(SysYParser.LAndExpContext ctx) {
        if (ctx.And()==null) {
            ctx.quad = ctx.eqExp().quad;
            ctx.address=ctx.eqExp().address;

            ctx.trueList=ctx.eqExp().trueList;
            ctx.falseList=ctx.eqExp().falseList;
        }else{
            ctx.quad = ctx.lAndExp().quad;
            ctx.trueList=ctx.eqExp().trueList;
            ctx.falseList = mergeList(ctx.lAndExp().falseList,ctx.eqExp().falseList);

            InterRepresent targetIR = irCodes.getInterRepresent(ctx.eqExp().quad);
            // 回填
            for (GotoRepresent ir : ctx.lAndExp().trueList) {
                ir.targetHolder.setInterRepresent(targetIR);
            }
        }
    }

    @Override
    public void enterLOrExp(SysYParser.LOrExpContext ctx) {

    }

    @Override
    public void exitLOrExp(SysYParser.LOrExpContext ctx) {
        if (ctx.Or()==null) {
            ctx.quad = ctx.lAndExp().quad;
            ctx.address=ctx.lAndExp().address;

            ctx.trueList=ctx.lAndExp().trueList;
            ctx.falseList=ctx.lAndExp().falseList;
        }else{
            ctx.quad = ctx.lOrExp().quad;
            ctx.trueList = mergeList(ctx.lOrExp().trueList,ctx.lAndExp().trueList);
            ctx.falseList = ctx.lAndExp().falseList;

            InterRepresent targetIR = irCodes.getInterRepresent(ctx.lAndExp().quad);
            // 回填
            for (GotoRepresent ir : ctx.lOrExp().falseList) {
                ir.targetHolder.setInterRepresent(targetIR);
            }
        }
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
        if(parserRuleContext instanceof SysYParser.StmtContext)
        {
            SysYParser.StmtContext stmtContext=(SysYParser.StmtContext) parserRuleContext;
            stmtContext.startStmtHolder = new InterRepresentHolder(null);
            irCodes.bookVacancy(stmtContext.startStmtHolder);
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
