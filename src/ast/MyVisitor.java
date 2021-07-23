package ast;

import antlr.SysYBaseVisitor;
import antlr.SysYLexer;
import antlr.SysYParser;
import ast.symbol.Domain;
import ast.symbol.Function;
import ast.symbol.Variable;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyVisitor extends SysYBaseVisitor<AstNode> {

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
                    Domain.addVariable(id);
                } else {
                    List<Integer> dimensions = new ArrayList<>();
                    dimensions.add(-1); // 首维为空
                    for (SysYParser.ExpContext expCtx : paramCtx.exp()) {
                        Integer value = visit(expCtx).getInteger();
                        dimensions.add(value);
                    }
                    Domain.addArray(id, dimensions);
                }
            }
        AstNode right = visit(ctx.block());
        // 离开
        Domain.leaveFunc();
        if (!OP.RETURN.getVal().equals(right.getRight().value.getVal())) {
            right.addNode(AstNode.makeLeaf(OP.RETURN));
        }
        return AstNode.makeBinaryNode(OP.FUNC_DECL, AstNode.makeLeaf(function), right);
    }


    @Override
    public AstNode visitBlock(SysYParser.BlockContext ctx) {
        Domain.enterDomain();
        AstNode ret = AstNode.makeEmptyNode(OP.STATEMENT);
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
        for (ParseTree child : ctx.children) {
            ret.addNode(visit(child));
        }
        return ret;
    }

    @Override
    public AstNode visitWhileStat(SysYParser.WhileStatContext ctx) {
        AstNode left = visit(ctx.cond());
        AstNode right = visit(ctx.stmt());
        return AstNode.makeBinaryNode(OP.WHILE, left, right);
    }

    @Override
    public AstNode visitLOrExp(SysYParser.LOrExpContext ctx) {
        if (null == ctx.Or()) {
            return visit(ctx.lAndExp());
        } else {
            AstNode left = visit(ctx.lOrExp());
            AstNode right = visit(ctx.lAndExp());
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
    public AstNode visitVarDecl(SysYParser.VarDeclContext ctx) {
        AstNode ret = AstNode.makeEmptyNode(OP.VAR_DECL);
        for (SysYParser.VarDefContext defCtx : ctx.varDef()) {
            ret.addNode(visit(defCtx));
        }

        return ret;
    }

    @Override
    public AstNode visitVarDef(SysYParser.VarDefContext ctx) {
        if (ctx.constExp().isEmpty()) { // 变量
            Variable variable = Domain.addVariable(ctx.Identifier().getText());
            if (null == ctx.initVal()) {
                return AstNode.makeLeaf(variable);
            }
            AstNode rVal = visit(ctx.initVal());

            return AstNode.makeBinaryNode(OP.ASSIGN, AstNode.makeLeaf(variable), rVal);
        } else {    // 数组
            List<Integer> dimensions = new ArrayList<>();
            for (SysYParser.ConstExpContext expCtx : ctx.constExp()) {  // 获取维度信息
                dimensions.add(visit(expCtx).getInteger());
            }
            Variable variable = Domain.addArray(ctx.Identifier().getText(), dimensions);
            if (null == ctx.initVal()) {
                return AstNode.makeLeaf(variable);
            }
            AstNode initVal = visit(ctx.initVal());    // 获取初始化值

            return AstNode.makeBinaryNode(OP.ASSIGN, AstNode.makeLeaf(variable), initVal);
        }
    }

    @Override
    public AstNode visitConstDecl(SysYParser.ConstDeclContext ctx) {
        AstNode ret = AstNode.makeEmptyNode(OP.CONST_DECL);
        for (SysYParser.ConstDefContext defCtx : ctx.constDef()) {
            ret.addNode(visit(defCtx));
        }

        return ret;
    }

    @Override
    public AstNode visitConstDef(SysYParser.ConstDefContext ctx) {
        if (ctx.constExp().isEmpty()) { // 符号常量
            //todo 可以加入更多验证 左右值检查
            AstNode rVal = visit(ctx.constInitVal());
            Variable variable = Domain.addConstVar(ctx.Identifier().getText());
            if (variable.isCollapsible()) {    // 可折叠的
                variable.addConstVal(((Immediate) rVal.value).value);
                System.out.println(variable);
            }
            return AstNode.makeBinaryNode(OP.ASSIGN, AstNode.makeLeaf(variable), rVal);
        } else {    // 数组常量
            List<Integer> dimensions = new ArrayList<>();
            for (SysYParser.ConstExpContext expCtx : ctx.constExp()) {  // 获取维度信息
                dimensions.add(visit(expCtx).getInteger());
            }
            Variable variable = Domain.addConstArray(ctx.Identifier().getText(), dimensions);
            AstNode initVal = visit(ctx.constInitVal());    // 获取初始化值
            if (variable.isCollapsible()) {    // 可折叠的
                Utils.assignArray(variable, initVal, variable.dimensions.size() - 1);
                System.out.println(variable);
            }

            return AstNode.makeBinaryNode(OP.ASSIGN, AstNode.makeLeaf(variable), initVal);
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
            List<Integer> base = var.dimensions;
            AstNode[] idx = new AstNode[base.size()];
            for (int i = 0; i < idx.length; i++) {
                idx[i] = visit(ctx.exp(i));
            }
            AstNode offset = Utils.getOffset(idx, base);

            return AstNode.makeBinaryNode(OP.OFFSET, AstNode.makeLeaf(var), offset);
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
        return Utils.calc(ret);
    }

    @Override
    public AstNode visitExp(SysYParser.ExpContext ctx) {
        AstNode ret = visit(ctx.addExp());
        return Utils.calc(ret);
    }

    @Override
    public AstNode visitCond(SysYParser.CondContext ctx) {
        AstNode ret = visit(ctx.lOrExp());
        return Utils.calc(ret);
    }
}
