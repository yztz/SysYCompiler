package compiler.symboltable;

import antlr.SysYParser;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.ListenerUtil;
import compiler.symboltable.function.FuncSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

import static compiler.genir.code.ListenerUtil.getDimsFromConstExp;

public class SysFuncConstSymbolListener extends SysExpCalListener {
    private final Stack<SymbolDomain> blockStack = new Stack<>();
    private SymbolTable currentSymbolTable;
    private FuncSymbol currentFunc = null;
    public FuncSymbolTable funcSymbolTable;
    public SymbolTableHost symbolTableHost;

    public SysFuncConstSymbolListener(SymbolTableHost symbolTableHost, FuncSymbolTable funcSymbolTable) {
        super(symbolTableHost);
        this.funcSymbolTable = funcSymbolTable;
        this.symbolTableHost = symbolTableHost;
        blockStack.push(SymbolDomain.globalDomain);
        currentSymbolTable = SymbolDomain.globalDomain.symbolTable;
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
            if(defCtx.constExp()==null || defCtx.constExp().size()==0) //不是数组
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

        pushNewScope();
    }

    private void pushNewScope()
    {
        SymbolDomain scope;
        if (blockStack.empty()) {
            scope = symbolTableHost.createSymbolDomain(null, currentFunc);
        }else{
            SymbolDomain topDomain = blockStack.peek();
            scope = symbolTableHost.createSymbolDomain(topDomain, currentFunc);
        }
        blockStack.push(scope);
        currentSymbolTable = scope.symbolTable;
    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {


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
                    AddressOrData[] dims= new AddressOrData[paramCtx.exp().size()+1];
                    dims[0] = null; //-1表示未知, 第一个方括号内没有参数
                    for (int expIndex = 0; expIndex < paramCtx.exp().size(); expIndex++) {
                        if (paramCtx.exp(expIndex).result!=null&&
                                paramCtx.exp(expIndex).result.isData) {
                            dims[expIndex+1] = new AddressOrData(true,paramCtx.exp(expIndex).result.item);
                        }else{
                            dims[expIndex+1] = null;
                            //System.err.println("Array size must be constant");
                        }
                    }
                    currentSymbolTable.addParamArray(identifier.getSymbol(), dims);
                }
            }
        }else{
            pushNewScope();
        }
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        blockStack.pop();
        SymbolDomain domain = blockStack.peek();
        currentSymbolTable = domain.symbolTable;
    }

    @Override
    public void exitExp(SysYParser.ExpContext ctx) {
        ctx.result = ctx.addExp().result;
    }

    @Override
    public void exitPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        if(ctx.lVal()!=null)
        {
            SysYParser.LValContext lValCtx = ctx.lVal();
            for (SysYParser.ExpContext expCtx : lValCtx.exp()) {
                if (expCtx.result==null) { //当前无法直接计算偏移量
                    return;
                }
            }
            HasInitSymbol symbol = (HasInitSymbol)symbolTableHost.searchSymbol(ctx.scope, lValCtx.Identifier().getSymbol(),
                                                              s->s instanceof HasInitSymbol);
            if(symbol==null)
                return;
            ListenerUtil.SymbolWithOffset<HasInitSymbol> symbolAndOffset = ListenerUtil.getSymbolAndOffset(symbol,
                                                                                               lValCtx);
            if(symbolAndOffset==null)
            {
                //System.err.println("Can't find symbol:"+ctx.lVal().Identifier().getSymbol().getText());
                return;
            }


            if(symbolAndOffset.symbol instanceof ConstSymbol
                    && symbolAndOffset.offsetResult.isData)
            {
                ctx.result =
                        new AddressOrData (true,
                                           ((ConstSymbol) symbolAndOffset.symbol).initValues[symbolAndOffset.offsetResult.item]);
            }
        }else if(ctx.exp()!=null) {
            ctx.result = ctx.exp().result;
        }
    }


    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
        if(parserRuleContext instanceof SysYParser.DomainedContext)
        {
            ((SysYParser.DomainedContext)parserRuleContext).scope =blockStack.peek();
        }
    }
}
