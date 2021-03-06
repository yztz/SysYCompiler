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
import java.util.LinkedList;
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
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.scope, varDefCtx.Identifier().getSymbol());
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
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.scope, ctx.Identifier().getSymbol());

            symbol.initIR = _currentCollection;
            _currentCollection = irCollectionStack.pop();
            if(!symbol.hasConstInitValue)
            {
                ctx.setStartStmt(new InterRepresentHolder(symbol.initIR.getFirst()));
                _currentCollection.addCodes(symbol.initIR);
            }else{
                InitVarRepresent initIR = new InitVarRepresent(symbol);
                ctx.setStartStmt(new InterRepresentHolder(initIR));
                _currentCollection.addCode(initIR,ctx.stop, "Init var:"+symbol.symbolToken.getText());
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
            VarSymbol symbol = symbolTableHost.searchVarSymbol(ctx.scope, ctx.ident);

            SaveRepresent ir = InterRepresentFactory.createSaveRepresent(symbol, new AddressOrData(true, ctx.symbolOffset),
                                                                         initResult);
            _currentCollection.addCode(ir,ctx.start);
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


        _currentCollection.addCode(new ReturnRepresent(true),ctx.block().stop, "default return");
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
        irCollectionStack.push(_currentCollection);
        _currentCollection = new IRCollection();
    }

    @Override
    public void exitFuncFParam(SysYParser.FuncFParamContext ctx) {
        TerminalNode identifier = ctx.Identifier();
        ParamSymbol symbol = symbolTableHost.searchParamSymbol(ctx.scope, identifier.getSymbol());

        if(ctx.exp()!=null && ctx.exp().size()>0)
        {
            if(ctx.dimensions==null)
                ctx.dimensions=new AddressOrData[ctx.exp().size()+1];
            for (int i = 0; i < ctx.exp().size(); i++) {
                ctx.dimensions[i+1] = ctx.exp(i).result;
            }
        }else{
            ctx.dimensions=new AddressOrData[]{new AddressOrData(true,1)};
        }
        symbol.setIrToCalDimSize(_currentCollection,ctx.dimensions);

        _currentCollection = irCollectionStack.pop();
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
        ValueSymbol targetSymbol = symbolTableHost.searchSymbol(ctx.scope, lValCtx.Identifier().getSymbol());

        SaveRepresent saveRepresent;
        ListenerUtil.SymbolWithOffset<? extends ValueSymbol> symbolAndOffset;
        ListenerUtil.IrCalOffset offsetCalculatorGroup;

        if(targetSymbol instanceof HasInitSymbol)
        {
            symbolAndOffset =
                    ListenerUtil.getSymbolAndOffset((HasInitSymbol) targetSymbol,lValCtx);

            offsetCalculatorGroup = symbolAndOffset.getOffsetCalculatorGroup();
            _currentCollection.addCodes(offsetCalculatorGroup);
            if(lValCtx.startStmt==null)
            {
                if(offsetCalculatorGroup.getLineOccupied()>0)
                {
                    lValCtx.startStmt = new InterRepresentHolder(
                            offsetCalculatorGroup.getAllIR().get(0)
                    );
                }
            }

        }else if(targetSymbol instanceof ParamSymbol)
        {
            ParamSymbol paramSymbol = (ParamSymbol) targetSymbol;
            symbolAndOffset =
                    ListenerUtil.getSymbolAndOffset(paramSymbol, lValCtx);

            offsetCalculatorGroup = symbolAndOffset.getOffsetCalculatorGroup();
            _currentCollection.addCodes(offsetCalculatorGroup);
            if(lValCtx.startStmt==null)
            {
                if(offsetCalculatorGroup.getLineOccupied()>0)
                {
                    lValCtx.startStmt = new InterRepresentHolder(
                            offsetCalculatorGroup.getAllIR().get(0)
                    );
                }
            }

        }else{
            return ;
        }

        saveRepresent = InterRepresentFactory.createSaveRepresent(symbolAndOffset.symbol
                , offsetCalculatorGroup.offsetResult, sourceResult);



        _currentCollection.addCode(saveRepresent,ctx.Assign().getSymbol());

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
        if(ctx.exp()!=null)
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
        ctx.stmt(0).trueBodyOfIf = true;
        _currentCollection.startSection("jump depending on condition: "+ctx.cond().getText());
    }

    @Override
    public void exitIfStat(SysYParser.IfStatContext ctx) {
        if(ctx.stmt().size()==2 && ctx.stmt(1).getStartStmt()!=null) //有else,且不为空
        {
            InterRepresent elseStart=ctx.stmt(1).getStartStmt().getInterRepresent();
            for (GotoRepresent ir : ctx.cond().falseList) {
                ir.setTargetIR(elseStart);
            }

            /*if(ctx.stmt().get(0).getStartStmt()!=null)
            {
                // 需要插入一句goto，在if的代码块执行完成后跳过else代码块
                GotoRepresent skipElseStmtIR = new GotoRepresent(null);
                _currentCollection.startSection("skip else stmts");
                _currentCollection.insertBefore(skipElseStmtIR, elseStart, false);
                _currentCollection.bookVacancy(skipElseStmtIR.targetHolder);
            }*/
        }else{
            for (GotoRepresent ir : ctx.cond().falseList) {
                _currentCollection.bookVacancy(ir.targetHolder);
            }
        }

        if(ctx.stmt(0).doneList!=null)
        {
            for (GotoRepresent gotoIR : ctx.stmt(0).doneList) {
                _currentCollection.bookVacancy(gotoIR.targetHolder);
            }
            // 需要插入一句goto，在if的代码块执行完成后跳过else代码块
                /*GotoRepresent skipElseStmtIR = new GotoRepresent(null);
                _currentCollection.startSection("skip else stmts");
                _currentCollection.insertBefore(skipElseStmtIR, elseStart, false);
                _currentCollection.bookVacancy(skipElseStmtIR.targetHolder);*/
        }

        if(ctx.stmt().get(0).getStartStmt()!=null)
        {
            // 回填trueList
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.flag = 1;
                ir.setTargetIR(ctx.stmt(0).getStartStmt().getInterRepresent());
            }
        }else
        {
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.flag = 2;
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
        _currentCollection.addCode(gotoStart,ctx.stmt().stop, "while end");

        if(ctx.stmt().getStartStmt()!=null) //stmt可能一条语句都没有
        {
            InterRepresent stmtStartIR = ctx.stmt().getStartStmt().getInterRepresent();
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.flag = 3;
                ir.targetHolder.setInterRepresent(stmtStartIR);
            }

        }else{
            for (GotoRepresent ir : ctx.cond().trueList) {
                ir.flag = 4;
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
        _currentCollection.addCode(breakGoto,ctx.start, "break");
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

        _currentCollection.addCode(continueGoto,ctx.start, "continue");
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
        _currentCollection.addCode(returnRepresent,ctx.start);
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
                _currentFunction.addCode(gotoIR,ctx.lOrExp().start);
                if(address.item!=0)
                {
                    ctx.lOrExp().trueList.add(gotoIR);
                }else{
                    ctx.lOrExp().falseList.add(gotoIR);
                }
                ctx.startStmt = new InterRepresentHolder(gotoIR);
            }else{
                System.exit(159);
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
                                                         new AddressOrData(true, 0),10);
            _currentCollection.addCode(pair.get(0),ctx.start);
            _currentCollection.addCode(pair.get(1),null);
        }

        for (GotoRepresent ir : ctx.trueList) {
            ir.flag = 5;
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
            ValueSymbol symbol = symbolTableHost.searchSymbol(ctx.scope, lValCtx.Identifier().getSymbol());
            if(symbol!=null)
            {
                ListenerUtil.SymbolWithOffset<? extends ValueSymbol> symbolAndOffset;
                int dimLength = 0;
                if(symbol instanceof HasInitSymbol) //是变量常量，不是参数
                {

                    symbolAndOffset = ListenerUtil.getSymbolAndOffset((HasInitSymbol) symbol,lValCtx);
                    dimLength =((HasInitSymbol)symbol).dimensions.length;

                }else if(symbol instanceof ParamSymbol)//是参数
                {
                    ParamSymbol paramSymbol=(ParamSymbol) symbol;
                    symbolAndOffset = ListenerUtil.getSymbolAndOffset(paramSymbol,lValCtx);
                    dimLength = paramSymbol.getDimLength();

                }else{
                    return;
                }
                if(symbolAndOffset.symbol.isArray() &&
                        (lValCtx.exp()==null||lValCtx.exp().size()<dimLength))
                {//是数组，并且没有下标下标的数量小于定义时的数量，则取地址

                    /*if(symbol instanceof ParamSymbol)//是参数
                    {
                        ParamSymbol paramSymbol = (ParamSymbol) symbol;
                        if(paramSymbol.irToCalDimSize.getAllIR().size()>0)
                            _currentCollection.addCodes(paramSymbol.irToCalDimSize);
                    }*/

                    ListenerUtil.IrCalOffset offsetCalculator = symbolAndOffset.getOffsetCalculatorGroup();
                    _currentCollection.addCodes(offsetCalculator);

                    LAddrRepresent lAddrRepresent = InterRepresentFactory.createLAddrRepresent(
                            symbolAndOffset.symbol, offsetCalculator.offsetResult
                    );
                    _currentCollection.addCode(lAddrRepresent,ctx.start);
                    ctx.result = lAddrRepresent.target;

                    if (lValCtx.startStmt!=null) {
                        ctx.startStmt=lValCtx.startStmt;
                    }else{
                        if(offsetCalculator.getLineOccupied()>0)
                            ctx.startStmt=new InterRepresentHolder(offsetCalculator.getFirst());
                        else
                            ctx.startStmt=new InterRepresentHolder(lAddrRepresent);
                    }

                }else{ //否则则取对应值
                    /*if(symbol instanceof ParamSymbol)//Param支持动态dimSize
                    {
                        _currentCollection.addCodes(((ParamSymbol) symbol).irToCalDimSize);
                    }*/ //现已包含在offsetCalculator中

                    ListenerUtil.IrCalOffset offsetCalculator = symbolAndOffset.getOffsetCalculatorGroup();
                    _currentCollection.addCodes(offsetCalculator);
                    LoadRepresent loadRepresent = InterRepresentFactory.createLoadRepresent(symbolAndOffset.symbol
                            , offsetCalculator.offsetResult);
                    _currentCollection.addCode(loadRepresent,null);
                    ctx.result = loadRepresent.target;

                    if (lValCtx.startStmt!=null) {
                        ctx.startStmt=lValCtx.startStmt;
                    }else{
                        if(offsetCalculator.getLineOccupied()>0)
                            ctx.startStmt=new InterRepresentHolder(offsetCalculator.getFirst());
                        else
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

        _currentCollection.addCode(ir,ctx.start);
        ctx.result = ir.returnResult;
    }

    @Override
    public void enterSignExpr(SysYParser.SignExprContext ctx) {

    }

    @Override
    public void exitSignExpr(SysYParser.SignExprContext ctx) {
        if(ctx.result!=null && ctx.result.isData)
        {
            return;
        }
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
        _currentCollection.addCode(ir,ctx.start);
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
        if(ctx.result!=null && ctx.result.isData)
        {
            return;
        }
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
            _currentCollection.addCode(ir,ctx.start);
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
        if(ctx.result!=null && ctx.result.isData)
        {
            return;
        }
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
            _currentCollection.addCode(ir,ctx.start);
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
                                  AddressOrData address1, AddressOrData address2,int flag) {
        IfGotoRepresent ifGoto = new IfGotoRepresent(null, relOp, address1, address2);
        ifGoto.flag = -1;
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

            if(ctx.relExp().startStmt!=null)
                ctx.startStmt = ctx.relExp().startStmt;
            else if(ctx.addExp().startStmt!=null)
                ctx.startStmt = ctx.addExp().startStmt;

            if(!ctx.jump) //如果父节点还是RelExpContext， 说明出现了类似a<b<c这样的表达式
            {
                RelRepresent represent = InterRepresentFactory.createRelRepresent(ctx.relExp().address,
                                                                                     ctx.addExp().result, relOp);
                _currentFunction.addCode(represent,ctx.start);
                ctx.address = represent.target;

                if(ctx.startStmt == null)
                    ctx.startStmt = new InterRepresentHolder(represent);
            }else{

                List<InterRepresent> pair = createIfGotoPair(ctx, relOp, ctx.relExp().address, ctx.addExp().result,0);
                _currentCollection.addCode(pair.get(0),ctx.start);
                _currentCollection.addCode(pair.get(1),null);

                if(ctx.startStmt == null)
                    ctx.startStmt = new InterRepresentHolder(pair.get(0));
            }
        }
    }

    @Override
    public void enterEqExp(SysYParser.EqExpContext ctx) {
        if(ctx.jump && ctx.op==null)
            ctx.relExp().jump=true;
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

            IfGotoRepresent.RelOp relOp= null;
            if(ctx.Equal()!=null){
                relOp= IfGotoRepresent.RelOp.EQUAL;
            }else if(ctx.NotEqual()!=null){
                relOp= IfGotoRepresent.RelOp.NOT_EQUAL;
            }else{
                System.err.println("Unknown rel opcode");
            }

            if(ctx.eqExp().startStmt!=null)
                ctx.startStmt = ctx.eqExp().startStmt;
            else if(ctx.relExp().startStmt!=null)
                ctx.startStmt = ctx.relExp().startStmt;

            if(!ctx.jump) //如果父节点还是EqExpContext， 说明出现了类似a<b<c这样的表达式
            {
                RelRepresent represent = InterRepresentFactory.createRelRepresent(ctx.eqExp().address,
                                                                                  ctx.relExp().address, relOp);
                _currentFunction.addCode(represent,ctx.start);
                ctx.address = represent.target;

                if(ctx.startStmt == null)
                    ctx.startStmt = new InterRepresentHolder(represent);
            }else{
                List<InterRepresent> pair = createIfGotoPair(ctx, relOp, ctx.eqExp().address, ctx.relExp().address,1);
                _currentCollection.addCode(pair.get(0),ctx.start);
                _currentCollection.addCode(pair.get(1),null);

                if(ctx.startStmt==null)
                    ctx.startStmt = new InterRepresentHolder(pair.get(0));
            }
        }
    }



    @Override
    public void enterLAndExp(SysYParser.LAndExpContext ctx) {
        ctx.eqExp().jump = true;
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
                    _currentFunction.addCode(gotoIR,ctx.start);
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
                    System.exit(181);
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            } else if(ctx.eqExp().startStmt==null) //右边是常数
            {
                if(ctx.lAndExp().trueList==null)
                {
                    List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lAndExp().address, new AddressOrData(true, 0),2);

                    InterRepresent lEqStart =  ctx.eqExp().startStmt!=null?
                            ctx.eqExp().startStmt.getInterRepresent():
                            null;
                    if(lEqStart!=null)
                    {
                        _currentCollection.insertBefore(pair.get(0), lEqStart);
                        _currentCollection.insertBefore(pair.get(1), lEqStart);
                    }else{
                        _currentCollection.addCode(pair.get(0),ctx.lAndExp().start);
                        _currentCollection.addCode(pair.get(1),null);
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
                                                                 ctx.eqExp().address, new AddressOrData(true, 0),3);
                    _currentCollection.addCode(pair.get(0),ctx.eqExp().start);
                    _currentCollection.addCode(pair.get(1),null);
                    if (ctx.eqExp().startStmt==null) {
                        ctx.eqExp().startStmt= new InterRepresentHolder(pair.get(0));
                    }
                }
                if(ctx.lAndExp().address.isData)
                {
                    ctx.trueList = ctx.eqExp().trueList;
                    ctx.falseList = ctx.eqExp().falseList;
                    if(ctx.lAndExp().address.item==0) //零，恒为false, 直接添加到tfalseList
                    {
                        GotoRepresent gotoIR = new GotoRepresent(null); //无条件跳转了
                        _currentFunction.addCode(gotoIR,ctx.lAndExp().start);
                        ctx.startStmt = new InterRepresentHolder(gotoIR);
                        _currentFunction.insertBefore(gotoIR,
                                                      ctx.eqExp().startStmt.getInterRepresent());
                        ctx.falseList.add(gotoIR);
                    }else{
                        System.exit(182);
                        // 0 就什么都不做，没有影响
                        ctx.startStmt = ctx.eqExp().startStmt;
                    }
                }else{
                    System.err.println("lOrExp,表达式不是常数，但找不到对应的IR");
                }
            }else { //都不是常数

                if(ctx.lAndExp().trueList==null)
                {
                    List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lAndExp().address, new AddressOrData(true, 0),4);

                    InterRepresent lEqStart =  ctx.eqExp().startStmt!=null?
                            ctx.eqExp().startStmt.getInterRepresent():
                            null;
                    if(lEqStart!=null)
                    {
                        _currentCollection.insertBefore(pair.get(0), lEqStart);
                        _currentCollection.insertBefore(pair.get(1), lEqStart);
                    }else{
                        _currentCollection.addCode(pair.get(0),ctx.lAndExp().start);
                        _currentCollection.addCode(pair.get(1),null);
                    }
                }
                if(ctx.eqExp().trueList==null){
                    List<InterRepresent> pair = createIfGotoPair(ctx.eqExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.eqExp().address, new AddressOrData(true, 0),5);
                    _currentCollection.addCode(pair.get(0),ctx.eqExp().start);
                    _currentCollection.addCode(pair.get(1),null);
                }

                ctx.startStmt = ctx.lAndExp().startStmt;
                ctx.trueList=ctx.eqExp().trueList;
                ctx.falseList = mergeList(ctx.lAndExp().falseList,ctx.eqExp().falseList);

                // 回填
                for (GotoRepresent ir : ctx.lAndExp().trueList) {
                    ir.flag=6;
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
                    _currentFunction.addCode(gotoIR,ctx.lAndExp().start);
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
                                                                     ctx.lOrExp().address, new AddressOrData(true, 0)
                                ,6);
                        InterRepresent lAndStart = ctx.lAndExp().startStmt!=null? ctx.lAndExp().startStmt.getInterRepresent()
                                :null;
                        if(lAndStart!=null)
                        {
                            _currentCollection.insertBefore(pair.get(0), lAndStart);
                            _currentCollection.insertBefore(pair.get(1), lAndStart);
                        }else{
                            _currentCollection.addCode(pair.get(0),ctx.start);
                            _currentCollection.addCode(pair.get(1),null);
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
                                                                     ctx.lAndExp().address, new AddressOrData(true,
                                                                                                              0),7);
                        _currentCollection.addCode(pair.get(0),ctx.start);
                        _currentCollection.addCode(pair.get(1),null);
                    }

                    ctx.trueList = ctx.lAndExp().trueList;
                    ctx.falseList = ctx.lAndExp().falseList;
                    if(ctx.lOrExp().address.item!=0) //非零，恒为true, 直接添加到trueList
                    {
                        GotoRepresent gotoIR = new GotoRepresent(null); //无条件跳转了
                        _currentFunction.addCode(gotoIR,ctx.start);
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
                                                                 ctx.lOrExp().address, new AddressOrData(true, 0),8);
                    InterRepresent lAndStart = ctx.lAndExp().startStmt!=null? ctx.lAndExp().startStmt.getInterRepresent()
                            :null;
                    if(lAndStart!=null)
                    {
                        _currentCollection.insertBefore(pair.get(0), lAndStart);
                        _currentCollection.insertBefore(pair.get(1), lAndStart);
                    }else{
                        _currentCollection.addCode(pair.get(0),ctx.start);
                        _currentCollection.addCode(pair.get(1),null);
                    }

                }
                if(ctx.lAndExp().trueList==null){
                    List<InterRepresent> pair = createIfGotoPair(ctx.lAndExp(), IfGotoRepresent.RelOp.NOT_EQUAL,
                                                                 ctx.lAndExp().address, new AddressOrData(true, 0),9);
                    _currentCollection.addCode(pair.get(0),ctx.start);
                    _currentCollection.addCode(pair.get(1),null);
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
        if(parserRuleContext instanceof SysYParser.StmtContext)
        {
            SysYParser.StmtContext ctx = ((SysYParser.StmtContext) parserRuleContext);
            if(ctx.trueBodyOfIf)
            {
                GotoRepresent gotoRepresent = new GotoRepresent(null);
                _currentCollection.addCode(gotoRepresent,ctx.stop,"true body done");
                ctx.doneList = new LinkedList<>();
                ctx.doneList.add(gotoRepresent);
            }
        }
    }
}
