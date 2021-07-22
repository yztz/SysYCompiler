package ast;

import antlr.SysYBaseVisitor;
import antlr.SysYLexer;
import antlr.SysYParser;
import ast.symbol.Domain;
import ast.symbol.Domain.*;
import ast.symbol.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyVisitor extends SysYBaseVisitor<AstNode> {

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
    public AstNode visitConstDecl(SysYParser.ConstDeclContext ctx) {
        for (SysYParser.ConstDefContext defCtx : ctx.constDef()) {
            visit(defCtx);
        }
        return null;
    }

    @Override
    public AstNode visitConstDef(SysYParser.ConstDefContext ctx) {
        if (ctx.constExp().isEmpty()) { // 符号常量
            //todo 可以加入更多验证 左右值检查
            AstNode rVal = visit(ctx.constInitVal());
            int value = Utils.calc(rVal);
            Variable variable = Domain.addConstVar(ctx.Identifier().getText());
            variable.value[0] = value;

            System.out.println(variable.getVal() + " = " + value);
        } else {    // 数组常量
            List<Integer> dimensions = new ArrayList<>();
            for (SysYParser.ConstExpContext expCtx : ctx.constExp()) {  // 获取维度信息
                dimensions.add(Utils.calc(visit(expCtx)));
            }
            Variable variable = Domain.addConstArray(ctx.Identifier().getText(), dimensions);
            AstNode initVal = visit(ctx.constInitVal());    // 获取初始化值
            Utils.assignArray(variable, initVal, variable.dimensions.size() - 1);

            System.out.println(variable.getVal() + " = " + Arrays.toString(variable.value));
        }

        return null;
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
            return AstNode.makeLeaf(Utils.calc(visit(ctx.constExp())));
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
            //todo 可以优化一些常量折叠
            List<Integer> base = var.dimensions;
            AstNode[] idx = new AstNode[base.size()];
            for (int i = 0; i < idx.length; i++) {
                idx[i] = visit(ctx.exp(i));
            }
            AstNode offset = Utils.getOffset(idx, base);

            return AstNode.makeBinaryNode(OP.OFFSET, AstNode.makeLeaf(var), offset);
        } else {    // 非数组
            if (var.isConst)
                return AstNode.makeLeaf(var.value[0]);
            else
                return AstNode.makeLeaf(var);
        }

    }

//    @Override
//    public AstNode visitExp(SysYParser.ExpContext ctx) {
//        System.out.println("exp: " + super.visitExp(ctx));
//        return super.visitExp(ctx);
//    }
}
