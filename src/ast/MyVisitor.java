package ast;

import antlr.SysYBaseVisitor;
import antlr.SysYLexer;
import antlr.SysYParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class MyVisitor extends SysYBaseVisitor<AstNode> {




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
            switch (op){
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
        //todo
//        return m
        return null;
    }

//    @Override
//    public AstNode visitExp(SysYParser.ExpContext ctx) {
//        System.out.println("exp: " + super.visitExp(ctx));
//        return super.visitExp(ctx);
//    }
}
