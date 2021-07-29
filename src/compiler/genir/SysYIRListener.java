package compiler.genir;

import antlr.SysYListener;
import antlr.SysYParser;
import compiler.Util;
import compiler.genir.code.*;
import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.ExternalFuncSymbol;
import compiler.symboltable.function.FuncSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import compiler.symboltable.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * 深度优先遍历语法树
 * 将语法树翻译为中间代码
 */
public class SysYIRListener implements SysYListener {
    public SymbolTableHost symbolTableHost;
    public FuncSymbolTable funcSymbolTable;
    public IRUnion irUnion = new IRUnion();
    private IRCollection _currentCollection;
    private IRFunction _currentFunction;
    private Stack<IRCollection> irCollectionStack=new Stack<>();
    public SysYIRListener(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        this.symbolTableHost = symbolTableHost;
        this.funcSymbolTable = funcSymbolTable;
    }

    //public IRCode irCodes=new IRCode();

    @Override
    public void enterCompUnit(SysYParser.CompUnitContext ctx) {
        /*if (ctx.decl()!=null && ctx.decl().size()>0) {
            IRSection declGroup = new IRCollection("declare symbol");
            irUnion.addIR(declGroup);
            for (int i = 0; i < ctx.children.size(); i++) {

                if (ctx.children.get(i)instanceof SysYParser.DeclContext) {
                    if(i!=0 && !(ctx.children.get(i-1) instanceof SysYParser.DeclContext))
                    {
                        declGroup = new IRSection("declare symbol");
                        irUnion.addIR(declGroup);
                    }
                    // todo ((SysYParser.DeclContext) ctx.children.get(i)).irGroup=declGroup;
                }
            }
        }*/
    }

    @Override
    public void exitCompUnit(SysYParser.CompUnitContext ctx) {

    }

    @Override
    public void enterDecl(SysYParser.DeclContext ctx) {
        if(ctx.parent instanceof SysYParser.CompUnitContext)
        {
            _currentCollection = new IRCollection();
        }
    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx) {
        if(ctx.varDecl()!=null)
            ctx.setStartStmt(ctx.varDecl().getStartStmt());
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
        if (_currentCollection !=null) {
            _currentCollection.startSection("declare symbol");
        }
        for (SysYParser.VarDefContext varDefCtx : ctx.varDef()) {
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,varDefCtx.Identifier().getSymbol());
            //varDefCtx.irGroup = new IRGroup("Init variable:"+symbol.symbolToken.getText());
        }
    }

    @Override
    public void exitVarDecl(SysYParser.VarDeclContext ctx) {
        for (int i = 0; i < ctx.varDef().size(); i++) {
            if(ctx.varDef(i).getStartStmt()!=null)
            {
                ctx.setStartStmt(ctx.varDef(i).getStartStmt());
                break;
            }
        }
    }

    @Override
    public void enterVarDef(SysYParser.VarDefContext ctx) {
        irCollectionStack.push(_currentCollection);
        _currentCollection = new IRCollection();
    }

    @Override
    public void exitVarDef(SysYParser.VarDefContext ctx) {
        if(ctx.initVal()!=null)
        {
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,ctx.Identifier().getSymbol());

            symbol.initIR = _currentCollection;
            _currentCollection = irCollectionStack.pop();
            if(!symbol.hasConstInitValue)
            {
                ctx.setStartStmt(new InterRepresentHolder(symbol.initIR.getFirst()));
                _currentCollection.addCodes(symbol.initIR);
            }else{
                InitVarRepresent initIR = new InitVarRepresent(symbol);
                ctx.setStartStmt(new InterRepresentHolder(initIR));
                _currentCollection.addCode(initIR, "Init var:"+symbol.symbolToken.getText());
            }
        }else{
            _currentCollection = irCollectionStack.pop();
        }
    }

    @Override
    public void enterInitVal(SysYParser.InitValContext ctx) {

    }

    @Override
    public void exitInitVal(SysYParser.InitValContext ctx) {

        if(ctx.exp()!=null)
        {
            AddressOrData initResult = ctx.exp().result;
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,ctx.ident);

            SaveRepresent ir = InterRepresentFactory.createSaveRepresent(symbol, new AddressOrData(true, ctx.symbolOffset),
                                                                         initResult);
            _currentCollection.addCode(ir);
        }
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        int funSize = 0;
        if(ctx.funcFParams()!=null)
        {
            funSize  = ctx.funcFParams().funcFParam().size();
        }
        FuncSymbol funcSymbol = funcSymbolTable.getFuncSymbol(ctx.Identifier().getText(), funSize);
        _currentFunction = new IRFunction(funcSymbol);
        _currentCollection = _currentFunction;
        irUnion.addIR(_currentCollection);
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {

        // 其实根本不需要
        /*if(_currentIRFunc.getLineOccupied()==0||
            !(_currentIRFunc.getLast().getLastIR() instanceof ReturnRepresent))
        {*/
        _currentCollection.addCode(new ReturnRepresent(), "default return");
        /*}*/
        _currentFunction = null;
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

        for (SysYParser.BlockItemContext blockItemContext : ctx.blockItem()) {
            if (blockItemContext.getStartStmt()!=null) {
                ctx.setStartStmt(blockItemContext.getStartStmt());
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
            ctx.setStartStmt(ctx.stmt().getStartStmt());
        }else{
            ctx.setStartStmt(ctx.decl().getStartStmt());
        }
    }

    @Override
    public void enterAssignStat(SysYParser.AssignStatContext ctx) {
        //IRSection expIrSection = new IRSection("compute exp value:"+ctx.exp().getText());
        //IRGroup lValSection = new IRGroup("compute index of array:"+ctx.lVal().getText());
        _currentCollection.startSection("compute index of array:"+ctx.lVal().getText());
        //ctx.irGroup.addSection(expIrSection);
        //ctx.irGroup.addSection(lValSection);
        //ctx.exp().irSection = expIrSection;
        //ctx.lVal().irGroup = lValSection;
    }

    @Override
    public void exitAssignStat(SysYParser.AssignStatContext ctx) {


        AddressOrData sourceResult = ctx.exp().result;

        SysYParser.LValContext lValCtx = ctx.lVal();
        ListenerUtil.SymbolWithOffset symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbolTableHost, lValCtx);

        for (InterRepresent ir : symbolAndOffset.irToCalculateOffset) {
            _currentCollection.addCode(ir);
        }

        SaveRepresent saveRepresent = InterRepresentFactory.createSaveRepresent(symbolAndOffset.symbol
                , symbolAndOffset.offsetResult, sourceResult);
        _currentCollection.addCode(saveRepresent);

        if(ctx.lVal().startStmt!=null){
            ctx.setStartStmt(ctx.lVal().startStmt);
        }else if(ctx.exp()!=null && ctx.exp().startStmt!=null) //todo
        {
            ctx.setStartStmt(ctx.exp().startStmt);
        }else{
            ctx.setStartStmt(new InterRepresentHolder(saveRepresent));
        }
    }

    @Override
    public void enterSemiStat(SysYParser.SemiStatContext ctx) {

    }

    @Override
    public void exitSemiStat(SysYParser.SemiStatContext ctx) {
        ctx.setStartStmt(ctx.exp().startStmt);
    }

    @Override
    public void enterBlockStat(SysYParser.BlockStatContext ctx) {
    }

    @Override
    public void exitBlockStat(SysYParser.BlockStatContext ctx) {
        ctx.setBreakQuads(ctx.block().getBreakQuads());
        ctx.setContinueQuads(ctx.block().getContinueQuads());

        ctx.setStartStmt(ctx.block().getStartStmt());

    }

    @Override
    public void enterIfStat(SysYParser.IfStatContext ctx) {
        _currentCollection.startSection("jump depending on condition: "+ctx.cond().getText());
    }

    @Override
    public void exitIfStat(SysYParser.IfStatContext ctx) {
        if(ctx.stmt().size()==2) //有else
        {
            InterRepresent elseStart=ctx.stmt(1).getStartStmt().getInterRepresent();
            for (GotoRepresent ir : ctx.cond().falseList) {
                ir.setTargetIR(elseStart);
            }
            if(ctx.stmt().get(0).getStartStmt()!=null)
            {
                // 需要插入一句goto，在if的代码块执行完成后跳过else代码块
                GotoRepresent skipElseStmtIR = new GotoRepresent(null);

                _currentCollection.insertBefore(skipElseStmtIR, elseStart, "skip stmts in else block");
                _currentCollection.bookVacancy(skipElseStmtIR.targetHolder);
            }

        }else{
            for (GotoRepresent ir : ctx.cond().falseList) {
                _currentCollection.bookVacancy(ir.targetHolder);
            }
        }

        if(ctx.stmt().get(0).getStartStmt()!=null)
        {
            // 回填trueList
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.setTargetIR(ctx.stmt(0).getStartStmt().getInterRepresent());
            }
        }else
        {
            for (GotoRepresent ir : ctx.cond().trueList) {
                _currentCollection.bookVacancy(ir.targetHolder);
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

        ctx.setStartStmt(ctx.cond().startStmt);
    }

    @Override
    public void enterWhileStat(SysYParser.WhileStatContext ctx) {

        _currentCollection.startSection("compute while condition:"+ctx.cond().getText());
    }

    @Override
    public void exitWhileStat(SysYParser.WhileStatContext ctx) {
        InterRepresent whileStartIR = ctx.cond().startStmt.getInterRepresent();


        //_currentIRFunc.startSection(ctx.stmt().irSection);
        GotoRepresent gotoStart = new GotoRepresent(whileStartIR);
        _currentCollection.addCode(gotoStart, "while end");

        if(ctx.stmt().getStartStmt()!=null) //stmt可能一条语句都没有
        {
            InterRepresent stmtStartIR = ctx.stmt().getStartStmt().getInterRepresent();
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.targetHolder.setInterRepresent(stmtStartIR);
            }

        }else{
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.targetHolder.setInterRepresent(whileStartIR);
            }
        }
        for (GotoRepresent ir : ctx.cond().falseList) {
            _currentCollection.bookVacancy(ir.targetHolder);
        }

        if (ctx.stmt().getBreakQuads() != null) {
            for (InterRepresentHolder breakQuad : ctx.stmt().getBreakQuads()) {
                _currentCollection.bookVacancy(breakQuad);
            }
        }

        if(ctx.stmt().getContinueQuads()!=null)
        {
            for (InterRepresentHolder continueQuad : ctx.stmt().getContinueQuads()) {
                continueQuad.setInterRepresent(whileStartIR);
            }
        }


        ctx.setStartStmt(ctx.cond().startStmt);
    }

    @Override
    public void enterBreakStat(SysYParser.BreakStatContext ctx) {

    }

    @Override
    public void exitBreakStat(SysYParser.BreakStatContext ctx) {
        GotoRepresent breakGoto=new GotoRepresent(null);
        ctx.setBreakQuads(new ArrayList<>());
        ctx.getBreakQuads().add(breakGoto.targetHolder);
        _currentCollection.addCode(breakGoto, "break");
        ctx.setStartStmt(new InterRepresentHolder(breakGoto));
    }

    @Override
    public void enterContinueStat(SysYParser.ContinueStatContext ctx) {

    }

    @Override
    public void exitContinueStat(SysYParser.ContinueStatContext ctx) {
        GotoRepresent continueGoto=new GotoRepresent(null);
        ctx.setContinueQuads(new ArrayList<>());
        ctx.getContinueQuads().add(continueGoto.targetHolder);

        _currentCollection.addCode(continueGoto, "continue");
        ctx.setStartStmt(new InterRepresentHolder(continueGoto));
    }

    @Override
    public void enterReturnStat(SysYParser.ReturnStatContext ctx) {

    }

    @Override
    public void exitReturnStat(SysYParser.ReturnStatContext ctx) {
        ReturnRepresent returnRepresent;
        if (ctx.exp() != null) {
            returnRepresent =new ReturnRepresent(ctx.exp().result);
        }else{
            returnRepresent =new ReturnRepresent();
        }
        if (ctx.exp()!=null && ctx.exp().startStmt != null) {
            ctx.setStartStmt(ctx.exp().startStmt);
        }else{
            ctx.setStartStmt(new InterRepresentHolder(returnRepresent));
        }
        _currentCollection.addCode(returnRepresent);
    }

    @Override
    public void enterExp(SysYParser.ExpContext ctx) {
        _currentCollection.startSection("compute value of exp:"+ctx.getText());
    }

    @Override
    public void exitExp(SysYParser.ExpContext ctx) {
        ctx.result=ctx.addExp().result;
        ctx.startStmt = ctx.addExp().startStmt;
    }

    @Override
    public void enterCond(SysYParser.CondContext ctx) {

    }

    @Override
    public void exitCond(SysYParser.CondContext ctx) {
        // todo 表达式是一个常量的情况下，会出现空指针异常

        if(ctx.lOrExp().startStmt==null)
        {
            AddressOrData address = ctx.lOrExp().address;
            if(address.isData)
            {
                ctx.lOrExp().trueList = new ArrayList<>();
                ctx.lOrExp().falseList = new ArrayList<>();
                GotoRepresent gotoIR = new GotoRepresent(null);
                _currentFunction.addCode(gotoIR);
                if(address.item!=0)
                {
                    ctx.lOrExp().trueList.add(gotoIR);
                }else{
                    ctx.lOrExp().falseList.add(gotoIR);
                }
                ctx.startStmt = new InterRepresentHolder(gotoIR);
            }else{
                System.err.println("条件表达式不是常量，但找不到对应的IR");
            }

        }else{
            ctx.startStmt = ctx.lOrExp().startStmt;
        }

        ctx.trueList=ctx.lOrExp().trueList;
        ctx.falseList=ctx.lOrExp().falseList;

        //没有||&&等表达式，也没有><等比较，只有一个值的情况下，两个list都会等于null
        if(ctx.trueList==null && ctx.falseList==null)
        {
            List<InterRepresent> pair = createIfGotoPair(ctx, IfGotoRepresent.RelOp.NOT_EQUAL, ctx.lOrExp().address,
                                                         new AddressOrData(true, 0));
            _currentCollection.addCode(pair.get(0));
            _currentCollection.addCode(pair.get(1));
        }

        for (GotoRepresent ir : ctx.trueList) {
                _currentCollection.bookVacancy(ir.targetHolder);
                //System.out.println("true "+ir.lineNum);
        }
    }

    @Override
    public void enterLVal(SysYParser.LValContext ctx) {
        /*for (SysYParser.ExpContext context : ctx.exp()) {
            context.irGroup = ctx.irGroup;
        }*/
    }

    @Override
    public void exitLVal(SysYParser.LValContext ctx) {
        if (ctx.exp()!=null) {
            for (int i = 0; i < ctx.exp().size(); i++) {
                if(ctx.exp().get(i).startStmt==null)
                    continue;
                ctx.startStmt = ctx.exp().get(i).startStmt;
                break;
            }
        }
    }

    @Override
    public void enterPrimaryExp(SysYParser.PrimaryExpContext ctx) {

    }

    @Override
    public void exitPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        TerminalNode numTerminal = ctx.Integer_const();
        if(numTerminal!=null)
        {
            // 在ConstExpListener里已经完成了
            ctx.result = new AddressOrData(true, Util.getIntFromStr(numTerminal.getSymbol().getText()));
        }else if(ctx.lVal()!=null) //左值，变量
        {
            SysYParser.LValContext lValCtx = ctx.lVal();
            ListenerUtil.SymbolWithOffset symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbolTableHost, lValCtx);
            if(symbolAndOffset!=null)
            {
                //是数组，并且没有下标表达式，则取地址

                if(/*!(symbolAndOffset.symbol instanceof ParamSymbol) &&*/ symbolAndOffset.symbol.isArray() &&
                        (lValCtx.exp()==null||lValCtx.exp().size()<symbolAndOffset.symbol.dimensions.length))
                {
                    for (InterRepresent ir : symbolAndOffset.irToCalculateOffset) {
                        _currentCollection.addCode(ir);
                    }
                    LAddrRepresent lAddrRepresent = InterRepresentFactory.createLAddrRepresent(
                            symbolAndOffset.symbol, symbolAndOffset.offsetResult
                    );
                    _currentCollection.addCode(lAddrRepresent);
                    ctx.result = lAddrRepresent.target;
                    ctx.startStmt=new InterRepresentHolder(lAddrRepresent);
                }else{ //否则则取对应值
                    for (InterRepresent ir : symbolAndOffset.irToCalculateOffset) {
                        _currentCollection.addCode(ir);
                    }
                    LoadRepresent loadRepresent = InterRepresentFactory.createLoadRepresent(symbolAndOffset.symbol
                            , symbolAndOffset.offsetResult);
                    _currentCollection.addCode(loadRepresent);
                    ctx.result = loadRepresent.target;
                    if (lValCtx.startStmt!=null) {
                        ctx.startStmt=lValCtx.startStmt;
                    }else{
                        ctx.startStmt=new InterRepresentHolder(loadRepresent);
                    }
                }

            }
        }else{
            ctx.result=ctx.exp().result;
            ctx.startStmt = ctx.exp().startStmt;
        }
    }

    @Override
    public void enterPrimaryExpr(SysYParser.PrimaryExprContext ctx) {

    }

    @Override
    public void exitPrimaryExpr(SysYParser.PrimaryExprContext ctx) {
        ctx.result=ctx.primaryExp().result;
        ctx.startStmt = ctx.primaryExp().startStmt;
    }

    @Override
    public void enterFunctionExpr(SysYParser.FunctionExprContext ctx) {

    }

    @Override
    public void exitFunctionExpr(SysYParser.FunctionExprContext ctx) {
        TerminalNode identifier = ctx.Identifier();
        AbstractFuncSymbol funcSymbol=funcSymbolTable.getFuncSymbol(identifier.getSymbol().getText(),
                                                                    ctx.funcRParams() != null?ctx.funcRParams().exp().size():0);
        if(funcSymbol==null)
            funcSymbol = new ExternalFuncSymbol(identifier.getSymbol().getText());

        CallRepresent ir;
        if (ctx.funcRParams() != null && ctx.funcRParams().exp().size()>0) {
            List<SysYParser.ExpContext> expContextList = ctx.funcRParams().exp();
            AddressOrData[] params = new AddressOrData[expContextList.size()];
            for (int i = 0; i < expContextList.size(); i++) {
                params[i] = expContextList.get(i).result;

                if(ctx.startStmt==null)
                {
                    //找到一个不是null的为止
                    ctx.startStmt = ctx.funcRParams().exp(i).startStmt;
                }
            }
            ir = InterRepresentFactory.createFuncCallRepresent(funcSymbol,params);


        }else{
            ir = InterRepresentFactory.createFuncCallRepresent(funcSymbol);
        }
        if(ctx.startStmt==null)
        {
            ctx.startStmt = new InterRepresentHolder(ir);
        }
        (_currentFunction).funcSymbol.setHasFuncCallInside(true);

        _currentCollection.addCode(ir);
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
        _currentCollection.addCode(ir);
        ctx.result=ir.target;

        if(ctx.unaryExp().startStmt!=null)
            ctx.startStmt = ctx.unaryExp().startStmt;
        else
            ctx.startStmt = new InterRepresentHolder(ir);
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
            ctx.startStmt = ctx.unaryExp().startStmt;
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
            _currentCollection.addCode(ir);
            ctx.result = ir.target;

            if(ctx.mulExp().startStmt!=null)
                ctx.startStmt = ctx.mulExp().startStmt;
            else if(ctx.unaryExp().startStmt!=null)
                ctx.startStmt = ctx.unaryExp().startStmt;
            else{
                ctx.startStmt = new InterRepresentHolder(ir);
            }
        }
    }

    @Override
    public void enterAddExp(SysYParser.AddExpContext ctx) {

    }

    @Override
    public void exitAddExp(SysYParser.AddExpContext ctx) {
        if (ctx.op==null) {
            ctx.result=ctx.mulExp().result;
            ctx.startStmt = ctx.mulExp().startStmt;
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
            _currentCollection.addCode(ir);
            ctx.result = ir.target;

            if(ctx.addExp().startStmt!=null)
                ctx.startStmt = ctx.addExp().startStmt;
            else if(ctx.mulExp().startStmt!=null)
                ctx.startStmt = ctx.mulExp().startStmt;
            else{
                ctx.startStmt = new InterRepresentHolder(ir);
            }
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
        /*ctx.vocancy = new InterRepresentHolder();
        ctx.irGroup.bookVacancy(ctx.vocancy);*/
    }

    private List<InterRepresent> createIfGotoPair(SysYParser.BranchContextBase ctx, IfGotoRepresent.RelOp relOp,
                                  AddressOrData address1, AddressOrData address2) {
        IfGotoRepresent ifGoto = new IfGotoRepresent(null, relOp, address1, address2);
        GotoRepresent goTo = new GotoRepresent(null);
        ctx.trueList=new ArrayList<>();
        ctx.falseList=new ArrayList<>();
        ctx.trueList.add(ifGoto);
        ctx.falseList.add(goTo);

        List<InterRepresent> irs=new ArrayList<>();
        irs.add(ifGoto);
        irs.add(goTo);
        return irs;
    }
    @Override
    public void exitRelExp(SysYParser.RelExpContext ctx) {
        if (ctx.op==null) {
            ctx.address=ctx.addExp().result;
            ctx.startStmt = ctx.addExp().startStmt;
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

            List<InterRepresent> pair = createIfGotoPair(ctx, relOp, ctx.relExp().address, ctx.addExp().result);
            _currentCollection.addCode(pair.get(0));
            _currentCollection.addCode(pair.get(1));

            if(ctx.relExp().startStmt!=null)
                ctx.startStmt = ctx.relExp().startStmt;
            else if(ctx.addExp().startStmt!=null)
                ctx.startStmt = ctx.addExp().startStmt;
            else
                ctx.startStmt = new InterRepresentHolder(pair.get(0));
        }
    }

    @Override
    public void enterEqExp(SysYParser.EqExpContext ctx) {

    }

    @Override
    public void exitEqExp(SysYParser.EqExpContext ctx) {
        if(ctx.op==null)
        {
            ctx.startStmt =ctx.relExp().startStmt;
            ctx.trueList=ctx.relExp().trueList;
            ctx.falseList=ctx.relExp().falseList;
            ctx.address=ctx.relExp().address;
        }else{

            /*ctx.irGroup.bookVacancy(ctx.startStmt);*/
            IfGotoRepresent.RelOp relOp= null;
            if(ctx.Equal()!=null){
                relOp= IfGotoRepresent.RelOp.EQUAL;
            }else if(ctx.NotEqual()!=null){
                relOp= IfGotoRepresent.RelOp.NOT_EQUAL;
            }else{
                System.err.println("Unknown rel opcode");
            }

            List<InterRepresent> pair = createIfGotoPair(ctx, relOp, ctx.eqExp().address, ctx.relExp().address);
            _currentCollection.addCode(pair.get(0));
            _currentCollection.addCode(pair.get(1));

            if(ctx.eqExp().startStmt!=null)
                ctx.startStmt = ctx.eqExp().startStmt;
            else if(ctx.relExp().startStmt!=null)
                ctx.startStmt = ctx.relExp().startStmt;
            else
                ctx.startStmt = new InterRepresentHolder(pair.get(0));
        }
    }



    @Override
    public void enterLAndExp(SysYParser.LAndExpContext ctx) {

    }

    @Override
    public void exitLAndExp(SysYParser.LAndExpContext ctx) {
        if (ctx.And()==null) {
            ctx.startStmt = ctx.eqExp().startStmt;
            ctx.address=ctx.eqExp().address;

            ctx.trueList=ctx.eqExp().trueList;
            ctx.falseList=ctx.eqExp().falseList;
        }else{


            if(ctx.lAndExp().startStmt==null && ctx.eqExp().startStmt==null) //两边都是常数
            {
                if(ctx.lAndExp().address.isData && ctx.eqExp().address.isData)
                {
                    GotoRepresent gotoIR = new GotoRepresent(null); //无条件跳转了
                    _currentFunction.addCode(gotoIR);
                    ctx.startStmt = new InterRepresentHolder(gotoIR);
                    if(ctx.lAndExp().address.item!=0 && ctx.eqExp().address.item!=0) //恒为true
                    {
                        List<GotoRepresent> trueList = new ArrayList<>();
                        trueList.add(gotoIR);
                        ctx.trueList = trueList;
                        ctx.falseList = new ArrayList<>();
                    }else{
                        List<GotoRepresent> falseList = new ArrayList<>();
                        falseList.add(gotoIR);
                        ctx.trueList = new ArrayList<>();
                        ctx.falseList = falseList;
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            } else if(ctx.eqExp().startStmt==null) //右边是常数
            {
                if(ctx.lAndExp().trueList==null)
                {
                    List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lAndExp().address, new AddressOrData(true, 0));

                    InterRepresent lEqStart =  ctx.eqExp().startStmt!=null?
                            ctx.eqExp().startStmt.getInterRepresent():
                            null;
                    if(lEqStart!=null)
                    {
                        _currentCollection.insertBefore(pair.get(0), lEqStart);
                        _currentCollection.insertBefore(pair.get(1), lEqStart);
                    }else{
                        _currentCollection.addCode(pair.get(0));
                        _currentCollection.addCode(pair.get(1));
                    }
                }

                ctx.startStmt  = ctx.lAndExp().startStmt;
                if(ctx.eqExp().address.isData)
                {
                    if(ctx.eqExp().address.item!=0) //非零，对整体不影响
                    {
                        ctx.trueList = ctx.lAndExp().trueList;
                        ctx.falseList = ctx.lAndExp().falseList;

                    }else{
                        ctx.trueList = new ArrayList<>();
                        //false，所以必定不执行
                        ctx.falseList = mergeList(ctx.lAndExp().trueList,
                                                 ctx.lAndExp().falseList);
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            }else if(ctx.lAndExp().startStmt==null) // 左边是常数
            {
                if(ctx.eqExp().trueList==null){
                    List<InterRepresent> pair = createIfGotoPair(ctx.eqExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.eqExp().address, new AddressOrData(true, 0));
                    _currentCollection.addCode(pair.get(0));
                    _currentCollection.addCode(pair.get(1));
                }
                if(ctx.lAndExp().address.isData)
                {
                    ctx.trueList = ctx.eqExp().trueList;
                    ctx.falseList = ctx.eqExp().falseList;
                    if(ctx.lAndExp().address.item==0) //零，恒为false, 直接添加到tfalseList
                    {
                        GotoRepresent gotoIR = new GotoRepresent(null); //无条件跳转了
                        _currentFunction.addCode(gotoIR);
                        ctx.startStmt = new InterRepresentHolder(gotoIR);
                        _currentFunction.insertBefore(gotoIR,
                                                      ctx.eqExp().startStmt.getInterRepresent());
                        ctx.falseList.add(gotoIR);
                    }else{
                        // 0 就什么都不做，没有影响
                        ctx.startStmt = ctx.lAndExp().startStmt;
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            }else { //都不是常数

                if(ctx.lAndExp().trueList==null)
                {
                    List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lAndExp().address, new AddressOrData(true, 0));

                    InterRepresent lEqStart =  ctx.eqExp().startStmt!=null?
                            ctx.eqExp().startStmt.getInterRepresent():
                            null;
                    if(lEqStart!=null)
                    {
                        _currentCollection.insertBefore(pair.get(0), lEqStart);
                        _currentCollection.insertBefore(pair.get(1), lEqStart);
                    }else{
                        _currentCollection.addCode(pair.get(0));
                        _currentCollection.addCode(pair.get(1));
                    }
                }
                if(ctx.eqExp().trueList==null){
                    List<InterRepresent> pair = createIfGotoPair(ctx.eqExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.eqExp().address, new AddressOrData(true, 0));
                    _currentCollection.addCode(pair.get(0));
                    _currentCollection.addCode(pair.get(1));
                }

                ctx.startStmt = ctx.lAndExp().startStmt;
                ctx.trueList=ctx.eqExp().trueList;
                ctx.falseList = mergeList(ctx.lAndExp().falseList,ctx.eqExp().falseList);

                // 回填
                for (GotoRepresent ir : ctx.lAndExp().trueList) {
                    ir.targetHolder=ctx.eqExp().startStmt;
                }
            }
        }
    }

    @Override
    public void enterLOrExp(SysYParser.LOrExpContext ctx) {

    }

    @Override
    public void exitLOrExp(SysYParser.LOrExpContext ctx) {
        if (ctx.Or()==null) {
            ctx.startStmt = ctx.lAndExp().startStmt;
            ctx.address=ctx.lAndExp().address;

            ctx.trueList=ctx.lAndExp().trueList;
            ctx.falseList=ctx.lAndExp().falseList;
        }else{


            if(ctx.lAndExp().startStmt==null && ctx.lOrExp().startStmt==null) //两边都是常数
            {
                if(ctx.lOrExp().address.isData && ctx.lAndExp().address.isData)
                {
                    GotoRepresent gotoIR = new GotoRepresent(null); //无条件跳转了
                    _currentFunction.addCode(gotoIR);
                    ctx.startStmt = new InterRepresentHolder(gotoIR);
                    if(ctx.lOrExp().address.item!=0 || ctx.lAndExp().address.item!=0) //恒为true
                    {
                        List<GotoRepresent> trueList = new ArrayList<>();
                        trueList.add(gotoIR);
                        ctx.trueList = trueList;
                        ctx.falseList = new ArrayList<>();
                    }else{
                        List<GotoRepresent> falseList = new ArrayList<>();
                        falseList.add(gotoIR);
                        ctx.falseList = falseList;
                        ctx.trueList = new ArrayList<>();
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            } else if(ctx.lAndExp().startStmt==null) //右边是常数
            {
                ctx.startStmt  = ctx.lOrExp().startStmt;
                if(ctx.lAndExp().address.isData)
                {
                    if(ctx.lOrExp().trueList==null)
                    {
                        List<InterRepresent> pair = createIfGotoPair(ctx.lOrExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                     ctx.lOrExp().address, new AddressOrData(true, 0));
                        InterRepresent lAndStart = ctx.lAndExp().startStmt!=null? ctx.lAndExp().startStmt.getInterRepresent()
                                :null;
                        if(lAndStart!=null)
                        {
                            _currentCollection.insertBefore(pair.get(0), lAndStart);
                            _currentCollection.insertBefore(pair.get(1), lAndStart);
                        }else{
                            _currentCollection.addCode(pair.get(0));
                            _currentCollection.addCode(pair.get(1));
                        }

                    }
                    if(ctx.lAndExp().address.item!=0) //非零，恒为true
                    {
                        //恒为true，所以必定执行
                        ctx.trueList = mergeList(ctx.lOrExp().trueList,
                                                 ctx.lOrExp().falseList);
                        ctx.falseList = new ArrayList<>();
                    }else{ //为0，对整体不影响,
                        ctx.trueList = ctx.lOrExp().trueList;
                        ctx.falseList = ctx.lOrExp().falseList;
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            }else if(ctx.lOrExp().startStmt==null) // 左边是常数
            {
                if(ctx.lOrExp().address.isData)
                {
                    if(ctx.lAndExp().trueList==null){
                        List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                     ctx.lAndExp().address, new AddressOrData(true, 0));
                        _currentCollection.addCode(pair.get(0));
                        _currentCollection.addCode(pair.get(1));
                    }

                    ctx.trueList = ctx.lAndExp().trueList;
                    ctx.falseList = ctx.lAndExp().falseList;
                    if(ctx.address.item!=0) //非零，恒为true, 直接添加到trueList
                    {
                        GotoRepresent gotoIR = new GotoRepresent(null); //无条件跳转了
                        _currentFunction.addCode(gotoIR);
                        ctx.startStmt = new InterRepresentHolder(gotoIR);
                        _currentFunction.insertBefore(gotoIR,
                                                      ctx.lAndExp().startStmt.getInterRepresent());
                        ctx.trueList = ctx.lAndExp().trueList;
                        ctx.trueList.add(gotoIR);
                    }else{
                        // 0 就什么都不做，没有影响
                        ctx.startStmt = ctx.lAndExp().startStmt;
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            }else { //都不是常数
                if(ctx.lOrExp().trueList==null)
                {
                    List<InterRepresent> pair = createIfGotoPair(ctx.lOrExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lOrExp().address, new AddressOrData(true, 0));
                    InterRepresent lAndStart = ctx.lAndExp().startStmt!=null? ctx.lAndExp().startStmt.getInterRepresent()
                            :null;
                    if(lAndStart!=null)
                    {
                        _currentCollection.insertBefore(pair.get(0), lAndStart);
                        _currentCollection.insertBefore(pair.get(1), lAndStart);
                    }else{
                        _currentCollection.addCode(pair.get(0));
                        _currentCollection.addCode(pair.get(1));
                    }

                }
                if(ctx.lAndExp().trueList==null){
                    List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lAndExp().address, new AddressOrData(true, 0));
                    _currentCollection.addCode(pair.get(0));
                    _currentCollection.addCode(pair.get(1));
                }

                ctx.startStmt = ctx.lOrExp().startStmt;
                ctx.trueList = mergeList(ctx.lOrExp().trueList,ctx.lAndExp().trueList);
                ctx.falseList = ctx.lAndExp().falseList;

                // 回填
                for (GotoRepresent ir : ctx.lOrExp().falseList) {
                    ir.targetHolder=ctx.lAndExp().startStmt;
                }
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
        /*if(parserRuleContext instanceof SysYParser.StmtContext)
        {
            SysYParser.StmtContext stmtContext=(SysYParser.StmtContext) parserRuleContext;
            stmtContext.setStartStmt(new InterRepresentHolder(null));
            //_currentCollection.bookVacancy(stmtContext.getStartStmt());
        }*/
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
    }
}
