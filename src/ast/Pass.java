package ast;

import java.util.function.Consumer;

public class Pass {

    public static void pass1(AstNode root) {
        Utils.traverse(root, (node -> {
            if (node.op == OP.IF_ELSE) resolveIfElse(node);
        }));
    }

    private static void resolveIfElse(AstNode ifStat) {
        AstNode cond = ifStat.getNode(0);
        AstNode then = ifStat.getNode(1);
        AstNode el = ifStat.getNode(2);

        if (cond.isLeaf() || cond.getLeft().isLeaf() && cond.getRight().isLeaf()) return;

        AstNode newIfElse;
        AstNode newThen, newEl;
        switch (cond.op) {
            case GE:
            case LE:
            case LT:
            case GT:
            case NOT_EQ:
            case EQ:
                break;
            case OR:
                // 重设条件
                ifStat.setNode(0, cond.getLeft());
                // 新建子if-else
                newIfElse = AstNode.makeEmptyNode(OP.IF_ELSE);
                // 将“或”右式作为新建if-else的条件
                newIfElse.addNode(cond.getRight());
                // 将then嵌入新建if-else的then语句
                newIfElse.addNode(then);
                //将else嵌入新建if-else的else语句
                newIfElse.addNode(el);
                // 重设原then
                newThen = AstNode.makeEmptyNode(OP.STATEMENT);
                newThen.addNode(AstNode.makeUnaryNode(OP.GOTO, then));
                ifStat.setNode(1, newThen);
                //重设原else
                newEl = AstNode.makeEmptyNode(OP.STATEMENT);
                newEl.addNode(newIfElse);
                ifStat.setNode(2, newThen);
                //todo
                break;
            case AND:
                // 重设条件
                ifStat.setNode(0, cond.getLeft());
                // 新建子if-else
                newIfElse = AstNode.makeEmptyNode(OP.IF_ELSE);
                // 将“与”右式作为新建if-else的条件
                newIfElse.addNode(cond.getRight());
                // 将then嵌入新建if-else的then语句
                newIfElse.addNode(then);
                // 新建if-else的else语句，创建goto跳转到原else
                newEl = AstNode.makeEmptyNode(OP.STATEMENT);
                newEl.addNode(AstNode.makeUnaryNode(OP.GOTO, el));
                // 重设原then
                newThen = AstNode.makeEmptyNode(OP.STATEMENT);
                newThen.addNode(newIfElse);
                ifStat.setNode(1, newThen);
                break;
        }
    }
}
