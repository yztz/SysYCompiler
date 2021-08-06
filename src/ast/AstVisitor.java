package ast;

import antlr.SysYBaseVisitor;
import antlr.SysYParser;
import common.OP;
import common.OffsetVar;
import common.symbol.Domain;
import common.symbol.Function;
import common.symbol.Variable;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class AstVisitor extends SysYBaseVisitor<AstNode> {

    @Override
    public AstNode visitCompUnit(SysYParser.CompUnitContext ctx) {
        AstNode root = AstNode.makeEmptyNode(OP.ROOT);
        for (ParseTree child : ctx.children) {
            root.addNode(visit(child));
        }

        return root;
    }

    @Override
    public AstNode visitFuncDef(SysYParser.FuncDefContext ctx) {
        //todo optimize the collections of params
        String name = ctx.Identifier().getText();
        String retType = ctx.funcType().getText();
        // 进入
        Function function = Domain.enterFunc(name, retType);
        if (null != ctx.funcFParams())
            for (SysYParser.FuncFParamContext paramCtx : ctx.funcFParams().params) {
                String id = paramCtx.Identifier().getText();
                if (0 == paramCtx.LeftBracket().size()) {
                    Domain.addParam(id);
                } else {
                    List<Integer> dimensions = new ArrayList<>();
                    dimensions.add(-1); // 首维为空
                    for (SysYParser.ExpContext expCtx : paramCtx.exp()) {
                        Integer value = visit(expCtx).getInteger();
                        dimensions.add(value);
                    }
                    Domain.addParam(id, dimensions);
                }
            }
        AstNode ret = visit(ctx.block());
        // 离开
        Domain.leaveFunc();
        if (ret.isLeaf() || ret.getRight().op != OP.RETURN) {
            ret.addNode(AstNode.makeLeaf(OP.RETURN));
        }
//        Utils.findFirstStat(ret).label = function;
        ret.label = function;
//        System.out.println(function.name + ": " + function.totalOffset);

        return ret;
    }


    @Override
    public AstNode visitBlock(SysYParser.BlockContext ctx) {
        Domain.enterDomain();
        AstNode ret = AstNode.makeEmptyNode(OP.STATEMENTS);
        for (SysYParser.BlockItemContext blockItemCtx : ctx.blockItem()) {
            ret.addNode(visit(blockItemCtx));
        }
        Domain.leaveDomain();
        return ret;
    }

    @Override
    public AstNode visitAssignStat(SysYParser.AssignStatContext ctx) {
        AstNode left = visit(ctx.lVal());
        AstNode right = visit(ctx.exp());
        return AstNode.makeBinaryNode(OP.ASSIGN, left, right);
    }

    @Override
    public AstNode visitBlockStat(SysYParser.BlockStatContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public AstNode visitIfStat(SysYParser.IfStatContext ctx) {
        AstNode ret = AstNode.makeEmptyNode(OP.IF_ELSE);
        ret.addNode(visit(ctx.cond()));
        // 添加完整的if-else
//        if (null != ctx.stmt(0)) {
//            ret.addNode(visit(ctx.stmt(0)));
//        } else {
//            ret.addNode(AstNode.makeEmptyNode(OP.STATEMENT));
//        }
        AstNode trueStat = visit(ctx.stmt(0));
        if (trueStat.op != OP.STATEMENTS) trueStat = AstNode.makeUnaryNode(OP.STATEMENTS, trueStat);
        ret.addNode(trueStat);
        if (null != ctx.stmt(1)) {
            AstNode falseStat = visit(ctx.stmt(1));
            if (falseStat.op != OP.STATEMENTS) falseStat = AstNode.makeUnaryNode(OP.STATEMENTS, falseStat);
            ret.addNode(falseStat);
        } else {
            ret.addNode(AstNode.makeEmptyNode(OP.STATEMENTS));
        }
        return ret;
    }

    @Override
    public AstNode visitSemiStat(SysYParser.SemiStatContext ctx) {
        if (null != ctx.exp()) {
            AstNode ret = visit(ctx.exp());
            if (ret.op == OP.CALL) {
                return ret;
            }
        }
        return null;
    }

    @Override
    public AstNode visitWhileStat(SysYParser.WhileStatContext ctx) {
        AstNode ret = AstNode.makeEmptyNode(OP.WHILE);
        ret.addNode(visit(ctx.cond()));
//        ret.addNode(AstNode.makeLeaf(Label.newLabel()));
        ret.addNode(visit(ctx.stmt()));
//        ret.addNode(AstNode.makeLeaf(Label.newLabel()));
        return ret;
    }

    @Override
    public AstNode visitLOrExp(SysYParser.LOrExpContext ctx) {
        if (null == ctx.Or()) {
            return visit(ctx.lAndExp());
        } else {
            AstNode left = visit(ctx.lOrExp());
            AstNode right = visit(ctx.lAndExp());

//            if (left.isLeaf()) left = AstNode.makeBinaryNode(OP.EQ, left, AstNode.makeLeaf(1));
//            if (right.isLeaf()) right = AstNode.makeBinaryNode(OP.EQ, right, AstNode.makeLeaf(1));

            return AstNode.makeBinaryNode(OP.OR, left, right);
        }
    }

    @Override
    public AstNode visitLAndExp(SysYParser.LAndExpContext ctx) {
        if (null == ctx.And()) {
            return visit(ctx.eqExp());
        } else {
            AstNode left = visit(ctx.lAndExp());
            AstNode right = visit(ctx.eqExp());

            if (left.isLeaf()) left = AstNode.makeBinaryNode(OP.EQ, left, AstNode.makeLeaf(1));
            if (right.isLeaf()) right = AstNode.makeBinaryNode(OP.EQ, right, AstNode.makeLeaf(1));

            return AstNode.makeBinaryNode(OP.AND, left, right);
        }
    }

    @Override
    public AstNode visitEqExp(SysYParser.EqExpContext ctx) {
        if (null == ctx.op) {
            return visit(ctx.relExp());
        } else {
            AstNode left = visit(ctx.eqExp());
            AstNode right = visit(ctx.relExp());
            if ("==".equals(ctx.op.getText())) {
                return AstNode.makeBinaryNode(OP.EQ, left, right);
            } else {
                return AstNode.makeBinaryNode(OP.NOT_EQ, left, right);
            }
        }
    }

    @Override
    public AstNode visitRelExp(SysYParser.RelExpContext ctx) {
        if (null == ctx.op) {
            return visit(ctx.addExp());
//            return AstNode.makeBinaryNode(OP.EQ, visit(ctx.addExp()), AstNode.makeLeaf(1));
        } else {
            AstNode left = visit(ctx.relExp());
            AstNode right = visit(ctx.addExp());
            switch (ctx.op.getText()) {
                case ">=":
                    return AstNode.makeBinaryNode(OP.GE, left, right);
                case "<=":
                    return AstNode.makeBinaryNode(OP.LE, left, right);
                case ">":
                    return AstNode.makeBinaryNode(OP.GT, left, right);
                case "<":
                    return AstNode.makeBinaryNode(OP.LT, left, right);
                default:
                    return null;
            }
        }
    }

    @Override
    public AstNode visitBreakStat(SysYParser.BreakStatContext ctx) {
        return AstNode.makeLeaf(OP.BREAK);
    }

    @Override
    public AstNode visitContinueStat(SysYParser.ContinueStatContext ctx) {
        return AstNode.makeLeaf(OP.CONTINUE);
    }

    @Override
    public AstNode visitReturnStat(SysYParser.ReturnStatContext ctx) {
        if (null == ctx.exp()) {
            return AstNode.makeLeaf(OP.RETURN);
        }
        AstNode ret = visit(ctx.exp());
        return AstNode.makeUnaryNode(OP.RETURN, ret);
    }

    @Override
    public AstNode visitDecl(SysYParser.DeclContext ctx) {
        AstNode ret = super.visitDecl(ctx);
        return Domain.globalDomain == Domain.getDomain() ? null : ret;
    }

    @Override
    public AstNode visitVarDecl(SysYParser.VarDeclContext ctx) {
        AstNode ret;
        List<AstNode> list = new ArrayList<>();
        for (SysYParser.VarDefContext defCtx : ctx.varDef()) {
            list.add(visit(defCtx));
        }
        if (list.size() == 1) {
            ret = list.get(0);
        } else {
            ret = AstNode.makeEmptyNode(OP.ASSIGN_GROUP);
            ret.addNode(list);
        }

        return ret;
    }

    @Override
    public AstNode visitVarDef(SysYParser.VarDefContext ctx) {
        if (ctx.constExp().isEmpty()) { // 变量
            Variable variable = Domain.addVariable(ctx.Identifier().getText());
            if (null == ctx.initVal()) {
                variable.isInit = false;
                return AstNode.makeLeaf(variable);
            }
            AstNode rVal = visit(ctx.initVal());
            if (variable.isGlobal())
                variable.addConstVal(rVal.getInteger());

            return AstNode.makeBinaryNode(OP.ASSIGN, AstNode.makeLeaf(variable), rVal);
        } else {    // 数组
            List<Integer> dimensions = new ArrayList<>();
            for (SysYParser.ConstExpContext expCtx : ctx.constExp()) {  // 获取维度信息
                dimensions.add(visit(expCtx).getInteger());
            }
            Variable variable = Domain.addArray(ctx.Identifier().getText(), dimensions);
            if (null == ctx.initVal()) {
                variable.isInit = false;
                return AstNode.makeLeaf(variable);
            }
            AstNode initVal = visit(ctx.initVal());    // 获取初始化值

            if (variable.isGlobal()) {    // 全局变量
                Utils.assignConstArray(variable, initVal, 0);
            }
            variable.pos = 0;
            AstNode ret = AstNode.makeEmptyNode(OP.ASSIGN_GROUP);
            Utils.assignArray(variable, initVal, 0, ret);
            return ret;
        }
    }


    @Override
    public AstNode visitConstDecl(SysYParser.ConstDeclContext ctx) {
        AstNode ret;
        List<AstNode> list = new ArrayList<>();
        for (SysYParser.ConstDefContext defCtx : ctx.constDef()) {
            list.add(visit(defCtx));
        }
        if (list.size() == 1) {
            ret = list.get(0);
        } else {
            ret = AstNode.makeEmptyNode(OP.ASSIGN_GROUP);
            ret.addNode(list);
        }

        return ret;
    }

    @Override
    public AstNode visitConstDef(SysYParser.ConstDefContext ctx) {
        if (ctx.constExp().isEmpty()) { // 符号常量
            //todo 可以加入更多验证 左右值检查
            AstNode rVal = visit(ctx.constInitVal());
            Variable variable = Domain.addConstVar(ctx.Identifier().getText());
//            if (variable.isGlobal()) {    // 全局变量
            variable.addConstVal(rVal.getInteger());
//            }
            return AstNode.makeBinaryNode(OP.ASSIGN, AstNode.makeLeaf(variable), rVal);
        } else {    // 数组常量
            List<Integer> dimensions = new ArrayList<>();
            for (SysYParser.ConstExpContext expCtx : ctx.constExp()) {  // 获取维度信息
                dimensions.add(visit(expCtx).getInteger());
            }
            Variable variable = Domain.addConstArray(ctx.Identifier().getText(), dimensions);
            AstNode initVal = visit(ctx.constInitVal());    // 获取初始化值
//            if (variable.isConst) {    // 全局变量
            Utils.assignConstArray(variable, initVal, 0);
//            }
            variable.pos = 0;
            AstNode ret = AstNode.makeEmptyNode(OP.ASSIGN_GROUP);
            Utils.assignArray(variable, initVal, 0, ret);
            return ret;
        }

    }

    @Override
    public AstNode visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        if (null == ctx.constExp()) {   // 新组
            AstNode node = AstNode.makeEmptyNode(OP.VAL_GROUP);
            for (SysYParser.ConstInitValContext subCtx : ctx.constInitVal()) {
                AstNode subNode = visit(subCtx);
                node.addNode(subNode);
            }
            return node;
        } else {    // 值
            return visit(ctx.constExp());
        }
    }

    @Override
    public AstNode visitInitVal(SysYParser.InitValContext ctx) {
        if (null == ctx.exp()) {   // 新组
            AstNode node = AstNode.makeEmptyNode(OP.VAL_GROUP);
            for (SysYParser.InitValContext subCtx : ctx.initVal()) {
                AstNode subNode = visit(subCtx);
                node.addNode(subNode);
            }
            return node;
        } else {    // 值
            return visit(ctx.exp());
        }
    }

    //    @Override
//    public AstNode visitConstExp(SysYParser.ConstExpContext ctx) {
//        AstNode
//    }

    @Override
    public AstNode visitAddExp(SysYParser.AddExpContext ctx) {

        AstNode right = visit(ctx.mulExp());
        if (null != ctx.addExp()) {
            AstNode left = visit(ctx.addExp());
            String op = ctx.op.getText();
            switch (op) {
                case "+":
                    return AstNode.makeBinaryNode(OP.ADD, left, right);
                case "-":
                    return AstNode.makeBinaryNode(OP.SUB, left, right);
            }
        }
        return right;
    }

    @Override
    public AstNode visitFunctionExpr(SysYParser.FunctionExprContext ctx) {
        AstNode left = AstNode.makeEmptyNode(OP.PARAM);
        if (null != ctx.funcRParams())
            for (SysYParser.ExpContext param : ctx.funcRParams().params) {
                left.addNode(visit(param));
            }
        AstNode right = AstNode.makeLeaf(Domain.searchFunc(ctx.Identifier().getText()));
        return AstNode.makeBinaryNode(OP.CALL, left, right);
    }


    @Override
    public AstNode visitMulExp(SysYParser.MulExpContext ctx) {

        AstNode right = visit(ctx.unaryExp());
        if (null != ctx.mulExp()) {
            AstNode left = visit(ctx.mulExp());
            String op = ctx.op.getText();
            switch (op) {
                case "*":
                    return AstNode.makeBinaryNode(OP.MUL, left, right);
                case "/":
                    return AstNode.makeBinaryNode(OP.DIV, left, right);
                case "%":
                    return AstNode.makeBinaryNode(OP.MOD, left, right);
            }
        }

        return right;
    }

    @Override
    public AstNode visitSignExpr(SysYParser.SignExprContext ctx) {
        AstNode sub = visit(ctx.unaryExp());
        String op = ctx.op.getText();
        switch (op) {
            case "+":
                return sub;
            case "-":
                return AstNode.makeUnaryNode(OP.MINUS, sub);
            case "!":
                return AstNode.makeUnaryNode(OP.NEGATE, sub);
            default:
                return null;
        }
    }

    @Override
    public AstNode visitPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        if (null != ctx.exp()) return visit(ctx.exp());
        if (null != ctx.Integer_const()) return AstNode.makeLeaf(ctx.Integer_const().getSymbol().getText());

        return visit(ctx.lVal());
    }

    @Override
    public AstNode visitLVal(SysYParser.LValContext ctx) {
        String id = ctx.Identifier().getText();
        Variable var = Domain.searchVar(id);

        if (var.isArray) {  // 数组
//            if ()
            List<Integer> base = var.dimensions;
            List<AstNode> idx = new ArrayList<>();
            for (int i = 0; ctx.exp(i) != null; i++) {
                idx.add(visit(ctx.exp(i)));
            }
            AstNode offset = Utils.getOffset(idx, base);
            if (null == offset)
                return AstNode.makeLeaf(var);
            else {
                OffsetVar offsetVar = new OffsetVar(var, offset);
                if (idx.size() < base.size()) offsetVar.isAddress = true;
                return AstNode.makeLeaf(offsetVar);
            }
        } else {    // 非数组
            if (var.isCollapsible())
                return AstNode.makeLeaf(var.indexConstVal(0));
            else
                return AstNode.makeLeaf(var);
        }

    }

    @Override
    public AstNode visitConstExp(SysYParser.ConstExpContext ctx) {
        AstNode ret = visit(ctx.addExp());
        return Utils.calc(ret); // todo 考虑在单一pass做折叠
    }

    @Override
    public AstNode visitExp(SysYParser.ExpContext ctx) {
        AstNode ret = visit(ctx.addExp());
        return Utils.calc(ret); // todo 考虑在单一pass做折叠
    }

    @Override
    public AstNode visitCond(SysYParser.CondContext ctx) {
        AstNode ret = visit(ctx.lOrExp());
        ret = Utils.calc(ret);  // todo 考虑在单一pass做折叠
//        if (ret.isLeaf()) ret = AstNode.makeBinaryNode(OP.EQ, ret, AstNode.makeLeaf(1));
        return ret;
    }
}
