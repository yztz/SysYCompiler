package compiler.genir;

import antlr.SysYListener;
import antlr.SysYParser;
import compiler.genir.code.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import compiler.symboltable.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 深度优先遍历语法树
 * 将语法树翻译为中间代码
 */
public class SysYIRListener implements SysYListener {
    public SymbolTableHost symbolTableHost;
    public FuncSymbolTable funcSymbolTable;
    public IRUnion irUnion = new IRUnion();
    private IRFunction _currentIRFunc;
    public SysYIRListener(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        this.symbolTableHost = symbolTableHost;
        this.funcSymbolTable = funcSymbolTable;
    }

    //public IRCode irCodes=new IRCode();

    @Override
    public void enterCompUnit(SysYParser.CompUnitContext ctx) {
        if (ctx.decl()!=null && ctx.decl().size()>0) {
            IRSection declGroup = new IRSection("declare symbol");
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
        }
    }

    @Override
    public void exitCompUnit(SysYParser.CompUnitContext ctx) {
//        for (SysYParser.FuncDefContext funcCtx : ctx.funcDef()) {
//            irUnion.addIR(funcCtx.irFunction);
//        }
    }

    @Override
    public void enterDecl(SysYParser.DeclContext ctx) {
        /*if (ctx.varDecl()!=null) {
            ctx.varDecl().irGroup = ctx.irGroup;
        }*/
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
        if (_currentIRFunc!=null) {
            _currentIRFunc.startSection("declare symbol");
        }
        for (SysYParser.VarDefContext varDefCtx : ctx.varDef()) {
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,varDefCtx.Identifier().getSymbol());
            varDefCtx.irGroup = new IRGroup("Init variable:"+symbol.symbolToken.getText());
        }
    }

    @Override
    public void exitVarDecl(SysYParser.VarDeclContext ctx) {

    }

    @Override
    public void enterVarDef(SysYParser.VarDefContext ctx) {
        if(ctx.initVal()!=null)
        {
            ctx.initVal().irGroup = ctx.irGroup;
        }
    }

    @Override
    public void exitVarDef(SysYParser.VarDefContext ctx) {
        if(ctx.initVal()!=null)
        {
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,ctx.Identifier().getSymbol());
            if(ctx.initVal().initValues!=null && ctx.initVal().initValues.length==1)
            {
                if(symbol!=null)
                {
                    /* todo 当只有一个数据时，直接生成一条中间表示
                    */
                    symbol.initIR = ctx.initVal().irGroup;

                    SaveRepresent ir = InterRepresentFactory.createSaveRepresent(symbol,
                                                                                 new AddressOrData(true, 0),
                                                                                 new AddressOrData(true,
                                                                                                   ctx.initVal().initValues[0]));

                    symbol.initIR.addCode(ir);
                }
            }
            if(_currentIRFunc!=null)
                _currentIRFunc.addSingleIR(new InitVarRepresent(symbol), "Init var:"+symbol.symbolToken.getText());
        }
    }

    @Override
    public void enterInitVal(SysYParser.InitValContext ctx) {
        if(ctx.initVal()!=null)
        {
            for (SysYParser.InitValContext initValCtx : ctx.initVal()) {
                initValCtx.irGroup = ctx.irGroup;
            }
        }
    }

    @Override
    public void exitInitVal(SysYParser.InitValContext ctx) {

        if(ctx.exp()!=null)
        {
            AddressOrData initResult = ctx.exp().result;
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.domain,ctx.ident);

            if (initResult!=null && initResult.isData) {
                SaveRepresent ir = InterRepresentFactory.createSaveRepresent(symbol, new AddressOrData(true, ctx.symbolOffset),
                                                                             initResult);
                ctx.irGroup.addCode(ir);
            }else{
                ctx.irGroup.merge(ctx.exp().irGroup);
                SaveRepresent ir = InterRepresentFactory.createSaveRepresent(symbol, ctx.exp().result, initResult);
                ctx.irGroup.addCode(ir);
            }
        }
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        int funSize = 0;
        if(ctx.funcFParams()!=null)
        {
            funSize  = ctx.funcFParams().funcFParam().size();
        }
        FuncSymbol funcSymbol = funcSymbolTable.getFuncSymbol(ctx.Identifier().getText(),funSize);
        _currentIRFunc = new IRFunction(funcSymbol);
        irUnion.addIR(_currentIRFunc);
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {

        // 其实根本不需要
        /*if(_currentIRFunc.getLineOccupied()==0||
            !(_currentIRFunc.getLast().getLastIR() instanceof ReturnRepresent))
        {
            IRGroup irGroup = new IRGroup("default return");
            irGroup.addCode(new ReturnRepresent());
            _currentIRFunc.addGroup(irGroup);
        }*/

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
        }
    }

    @Override
    public void enterAssignStat(SysYParser.AssignStatContext ctx) {
        //IRSection expIrSection = new IRSection("compute exp value:"+ctx.exp().getText());
        IRGroup lValSection = new IRGroup("compute index of array:"+ctx.lVal().getText());
        //ctx.irGroup.addSection(expIrSection);
        //ctx.irGroup.addSection(lValSection);
        //ctx.exp().irSection = expIrSection;
        ctx.lVal().irGroup = lValSection;
    }

    @Override
    public void exitAssignStat(SysYParser.AssignStatContext ctx) {
        _currentIRFunc.addGroup(ctx.exp().irGroup);
        _currentIRFunc.addGroup(ctx.lVal().irGroup);
        AddressOrData sourceResult = ctx.exp().result;

        SysYParser.LValContext lValCtx = ctx.lVal();
        ListenerUtil.SymbolWithOffset symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbolTableHost, lValCtx);
        if(symbolAndOffset!=null && symbolAndOffset.symbol instanceof VarSymbol)
        {
            for (InterRepresent ir : symbolAndOffset.irToCalculateOffset) {
                lValCtx.irGroup.addCode(ir);
            }
            SaveRepresent saveRepresent = InterRepresentFactory.createSaveRepresent((VarSymbol)symbolAndOffset.symbol
                    , symbolAndOffset.offsetResult,sourceResult);
            lValCtx.irGroup.addCode(saveRepresent);
        }
        ctx.startStmt=(ctx.exp().irGroup.getLineOccupied()>0?ctx.exp().irGroup:ctx.lVal().irGroup);
        ctx.endStmt = (ctx.lVal().irGroup);
    }

    @Override
    public void enterSemiStat(SysYParser.SemiStatContext ctx) {

    }

    @Override
    public void exitSemiStat(SysYParser.SemiStatContext ctx) {
        // todo 这是干啥的
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
        _currentIRFunc.startSection("if cond");
        ctx.cond().irGroup = new IRGroup("jump depending on condition: "+ctx.cond().getText());
    }

    @Override
    public void exitIfStat(SysYParser.IfStatContext ctx) {
        if(ctx.stmt().size()==2) //有else
        {
            IRGroup elseStart=ctx.stmt(1).startStmt;
            for (GotoRepresent ir : ctx.cond().falseList) {
                ir.setTargetIR(elseStart.getFirst());
            }
            // 需要插入一句goto，在if的代码块执行完成后跳过else代码块
            GotoRepresent skipElseStmtIR = new GotoRepresent(null);
            IRGroup elseGroup = new IRGroup("skip stmts in else block");
            elseGroup.addCode(skipElseStmtIR);

            _currentIRFunc.insertGroupBefore(elseGroup,elseStart);
            _currentIRFunc.bookVacancy(skipElseStmtIR.targetHolder);

        }else{
            for (GotoRepresent ir : ctx.cond().falseList) {
                _currentIRFunc.bookVacancy(ir.targetHolder);
            }
        }
        // 回填trueList
        for (GotoRepresent ir : ctx.cond().trueList) {
            ir.setTargetIR(ctx.stmt(0).startStmt.getFirst());
        }

        _currentIRFunc.endSection("if body");

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
        IRGroup condSection = new IRGroup("compute while condition:"+ctx.cond().getText());
        ctx.cond().irGroup = condSection;

        _currentIRFunc.startSection("while cond");
    }

    @Override
    public void exitWhileStat(SysYParser.WhileStatContext ctx) {
        InterRepresent whileStartIR = ctx.cond().irGroup.getFirst();


        //_currentIRFunc.startSection(ctx.stmt().irSection);
        GotoRepresent gotoStart = new GotoRepresent(whileStartIR);
        _currentIRFunc.addSingleIR(gotoStart,"while end");

        InterRepresent stmtStartIR = ctx.stmt().startStmt.getFirst();
        for (GotoRepresent ir : ctx.cond().trueList) {
            ir.targetHolder.setInterRepresent(stmtStartIR);
        }
        for (GotoRepresent ir : ctx.cond().falseList) {
            _currentIRFunc.bookVacancy(ir.targetHolder);
        }

        if (ctx.stmt().getBreakQuads() != null) {
            for (InterRepresentHolder breakQuad : ctx.stmt().getBreakQuads()) {
                _currentIRFunc.bookVacancy(breakQuad);
            }
        }

        if(ctx.stmt().getContinueQuads()!=null)
        {
            for (InterRepresentHolder continueQuad : ctx.stmt().getContinueQuads()) {
                continueQuad.setInterRepresent(whileStartIR);
            }
        }

        _currentIRFunc.endSection("while body");
    }

    @Override
    public void enterBreakStat(SysYParser.BreakStatContext ctx) {

    }

    @Override
    public void exitBreakStat(SysYParser.BreakStatContext ctx) {
        GotoRepresent breakGoto=new GotoRepresent(null);
        ctx.setBreakQuads(new ArrayList<>());
        ctx.getBreakQuads().add(breakGoto.targetHolder);
        IRGroup gotoGroup = new IRGroup("break");
        gotoGroup.addCode(breakGoto);
        _currentIRFunc.addGroup(gotoGroup);

        ctx.startStmt = ctx.endStmt = gotoGroup;
    }

    @Override
    public void enterContinueStat(SysYParser.ContinueStatContext ctx) {

    }

    @Override
    public void exitContinueStat(SysYParser.ContinueStatContext ctx) {
        GotoRepresent continueGoto=new GotoRepresent(null);
        ctx.setContinueQuads(new ArrayList<>());
        ctx.getContinueQuads().add(continueGoto.targetHolder);
        IRGroup gotoGroup = new IRGroup("continue");
        gotoGroup.addCode(continueGoto);

        _currentIRFunc.addGroup(gotoGroup);

        ctx.startStmt=ctx.endStmt = gotoGroup;
    }

    @Override
    public void enterReturnStat(SysYParser.ReturnStatContext ctx) {

    }

    @Override
    public void exitReturnStat(SysYParser.ReturnStatContext ctx) {
        ReturnRepresent returnRepresent;
        if (ctx.exp() != null) {
            _currentIRFunc.addGroup(ctx.exp().irGroup);
            returnRepresent =new ReturnRepresent(ctx.exp().result);
        }else{
            returnRepresent =new ReturnRepresent();
        }
        IRGroup retGroup = new IRGroup(ctx.getText());
        retGroup.addCode(returnRepresent);

        _currentIRFunc.addGroup(retGroup);
        ctx.startStmt = ctx.endStmt = retGroup;
    }

    @Override
    public void enterExp(SysYParser.ExpContext ctx) {
        if(ctx.irGroup==null)
        {
            ctx.irGroup = new IRGroup("compute value of exp:"+ctx.getText());
        }

        ctx.addExp().irGroup = ctx.irGroup;
    }

    @Override
    public void exitExp(SysYParser.ExpContext ctx) {
        ctx.result=ctx.addExp().result;
    }

    @Override
    public void enterCond(SysYParser.CondContext ctx) {
        for (ParseTree child : ctx.children) {
            if(child instanceof SysYParser.RelExpContextBase)
            {
                ((SysYParser.RelExpContextBase) child).irGroup = ctx.irGroup;
            }
        }
    }

    @Override
    public void exitCond(SysYParser.CondContext ctx) {
        ctx.trueList=ctx.lOrExp().trueList;
        ctx.falseList=ctx.lOrExp().falseList;
        for (GotoRepresent ir : ctx.trueList) {
            ctx.irGroup.bookVacancy(ir.targetHolder);
            //System.out.println("true "+ir.lineNum);
        }

        _currentIRFunc.addGroup(ctx.irGroup);
        _currentIRFunc.endSection();
    }

    @Override
    public void enterLVal(SysYParser.LValContext ctx) {
        /*for (SysYParser.ExpContext context : ctx.exp()) {
            context.irGroup = ctx.irGroup;
        }*/
    }

    @Override
    public void exitLVal(SysYParser.LValContext ctx) {
        for (SysYParser.ExpContext expCtx : ctx.exp()) {
            _currentIRFunc.addGroup(expCtx.irGroup);
        }

    }

    @Override
    public void enterPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        if (ctx.lVal()!=null) {
            ctx.lVal().irGroup = ctx.irGroup;
        }
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
            ListenerUtil.SymbolWithOffset symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbolTableHost, lValCtx);
            if(symbolAndOffset!=null)
            {

                for (InterRepresent ir : symbolAndOffset.irToCalculateOffset) {
                    ctx.irGroup.addCode(ir);
                }
                LoadRepresent loadRepresent = InterRepresentFactory.createLoadRepresent(symbolAndOffset.symbol
                        , symbolAndOffset.offsetResult);
                ctx.irGroup.addCode(loadRepresent);
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
        FuncSymbol funcSymbol=funcSymbolTable.getFuncSymbol(identifier.getSymbol().getText(),
                                                            ctx.funcRParams() != null?ctx.funcRParams().exp().size():0);
        if(funcSymbol==null)
            System.out.println("Function is not defined");

        CallRepresent ir;
        if (ctx.funcRParams() != null) {
            List<SysYParser.ExpContext> expContextList = ctx.funcRParams().exp();
            AddressOrData[] params = new AddressOrData[expContextList.size()];
            for (int i = 0; i < expContextList.size(); i++) {
                params[i] = expContextList.get(i).result;

                _currentIRFunc.addGroup(expContextList.get(i).irGroup); //把参数表达式添加到ir
            }
            ir = InterRepresentFactory.createFuncCallRepresent(funcSymbol,params);
        }else{
            ir = InterRepresentFactory.createFuncCallRepresent(funcSymbol);
        }

        _currentIRFunc.funcSymbol.setHasFuncCallInside(true);

        ctx.irGroup.addCode(ir);
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
        ctx.irGroup.addCode(ir);
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
            ctx.irGroup.addCode(ir);
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
            ctx.irGroup.addCode(ir);
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
        ctx.vocancy = new InterRepresentHolder();
        ctx.irGroup.bookVacancy(ctx.vocancy);
    }

    private void createIfGotoPair(SysYParser.BranchContextBase ctx, IfGotoRepresent.RelOp relOp,
                                  AddressOrData address1, AddressOrData address2) {
        IfGotoRepresent ifGoto = new IfGotoRepresent(null, relOp, address1, address2);
        GotoRepresent goTo = new GotoRepresent(null);
        ctx.trueList=new ArrayList<>();
        ctx.falseList=new ArrayList<>();
        ctx.trueList.add(ifGoto);
        ctx.falseList.add(goTo);
        ctx.irGroup.addCode(ifGoto);
        ctx.irGroup.addCode(goTo);
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
            ctx.vocancy =ctx.relExp().vocancy;
            ctx.trueList=ctx.relExp().trueList;
            ctx.falseList=ctx.relExp().falseList;
            ctx.address=ctx.relExp().address;
        }else{
            ctx.vocancy = new InterRepresentHolder();
            ctx.irGroup.bookVacancy(ctx.vocancy);
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
            ctx.vocancy = ctx.eqExp().vocancy;
            ctx.address=ctx.eqExp().address;

            ctx.trueList=ctx.eqExp().trueList;
            ctx.falseList=ctx.eqExp().falseList;
        }else{
            ctx.vocancy = ctx.lAndExp().vocancy;
            ctx.trueList=ctx.eqExp().trueList;
            ctx.falseList = mergeList(ctx.lAndExp().falseList,ctx.eqExp().falseList);

            // 回填
            for (GotoRepresent ir : ctx.lAndExp().trueList) {
                ir.targetHolder=ctx.eqExp().vocancy;
            }
        }
    }

    @Override
    public void enterLOrExp(SysYParser.LOrExpContext ctx) {

    }

    @Override
    public void exitLOrExp(SysYParser.LOrExpContext ctx) {
        if (ctx.Or()==null) {
            ctx.vocancy = ctx.lAndExp().vocancy;
            ctx.address=ctx.lAndExp().address;

            ctx.trueList=ctx.lAndExp().trueList;
            ctx.falseList=ctx.lAndExp().falseList;
        }else{
            ctx.vocancy = ctx.lOrExp().vocancy;
            ctx.trueList = mergeList(ctx.lOrExp().trueList,ctx.lAndExp().trueList);
            ctx.falseList = ctx.lAndExp().falseList;

            // 回填
            for (GotoRepresent ir : ctx.lOrExp().falseList) {
                ir.targetHolder=ctx.lAndExp().vocancy;
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
        // 每个表达式一个section, 由父节点向子节点传递，子节点在其中添加IR语句
        if(parserRuleContext instanceof SysYParser.ExpContextBase)
        {
            for (ParseTree child : parserRuleContext.children) {
                if(child instanceof SysYParser.ExpContextBase)
                {
                    ((SysYParser.ExpContextBase) child).irGroup =
                            ((SysYParser.ExpContextBase) parserRuleContext).irGroup;
                }
            }
        }
        if(parserRuleContext instanceof SysYParser.RelExpContextBase)
        {
            for (ParseTree child : parserRuleContext.children) {
                if(child instanceof SysYParser.RelExpContextBase)
                {
                    ((SysYParser.RelExpContextBase) child).irGroup =
                            ((SysYParser.RelExpContextBase) parserRuleContext).irGroup;
                }
                if(child instanceof SysYParser.ExpContextBase)
                {
                    ((SysYParser.ExpContextBase) child).irGroup =
                            ((SysYParser.RelExpContextBase) parserRuleContext).irGroup;
                }
            }
        }
//        if(parserRuleContext instanceof SysYParser.HasInterRepresentBase)
//        {
//            SysYParser.HasInterRepresentBase stmtContext=(SysYParser.HasInterRepresentBase) parserRuleContext;
//            stmtContext.startStmtHolder = new InterRepresentHolder(null);
//            if (stmtContext.parent instanceof SysYParser.PositionableBase) {
//                stmtContext.irSection = ((SysYParser.PositionableBase) stmtContext.parent).irSection;
//            }
//        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
        if (parserRuleContext instanceof SysYParser.PositionableBase) {
            SysYParser.PositionableBase ctx = (SysYParser.PositionableBase)parserRuleContext;
            for (ParseTree child : ctx.children) {
                if (!(child instanceof SysYParser.PositionableBase)) {
                    continue;
                }
                if(((SysYParser.PositionableBase) child).startStmt==null)
                {
                    continue;
                }
                ctx.startStmt=((SysYParser.PositionableBase) child).startStmt;
                break;
            }

            for (int i = ctx.children.size() - 1; i >= 0; i--) {
                ParseTree child = ctx.children.get(i);
                if (!(child instanceof SysYParser.PositionableBase)) {
                    continue;
                }
                if(((SysYParser.PositionableBase) child).endStmt == null)
                {
                    continue;
                }
                ctx.endStmt=((SysYParser.PositionableBase) child).endStmt;
                break;
            }
        }
    }
}
