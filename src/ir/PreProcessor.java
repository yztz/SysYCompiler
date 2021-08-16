package ir;

import ast.AstNode;
import ast.Immediate;
import ast.Utils;
import common.ILabel;
import common.Label;
import common.OP;

import java.util.List;

public class PreProcessor {
    /**
     * 处理while
     * 主要步骤：while -> if -> processIF
     */
    public static void processWhile(AstNode root) {
        /*
        这里的searchNode采用后续遍历，主要是为接下来多层嵌套循环服务的，保证break和continue的作用不乱
        */
        List<AstNode> whileStats = Utils.searchNode(root, OP.WHILE);
        whileStats.forEach(PreProcessor::resolveWhile);
    }

    /**
     * 处理if-else
     * 主要实现：短路计算；为false块增加goto跳转方便后续ir处理
     */
    public static void processIF(AstNode root) {
        List<AstNode> ifStats = Utils.searchNode(root, OP.IF_ELSE);
        // 制造短路
        ifStats.forEach(PreProcessor::resolveIfElse);
    }

    /*
        将指定分支下的语句替换if语句
     */
    private static void reduceIf(AstNode ifStat, boolean branch) {
        System.out.println("reduced...");
        AstNode parent = ifStat.parent;
        AstNode prev = ifStat;
        for (AstNode node : ifStat.getNode(branch ? 1 : 2).getSubTrees()) {
            if (node.op == OP.GOTO && node.value == ifStat.label) {
                AstNode firstNode = ifStat.getNode(branch ? 1 : 2).getLeft();
                if (firstNode.label == null)
                    firstNode.label = ((ILabel) node.value);
                else
                    node.value = firstNode.label;
            }
            parent.insertAfter(prev, node);
            prev = node;
        }
        parent.removeNode(ifStat);
    }


    private static void resolveIfElse(AstNode ifStat) {
        if (ifStat.op != OP.IF_ELSE) return;

        AstNode cond = ifStat.getNode(0);
        AstNode then = ifStat.getNode(1);
        AstNode el = ifStat.getNode(2);


        if (!OP.isRelOP(cond.op)) {    // 处理单值的情况  if(a)...
            cond = AstNode.makeBinaryNode(OP.NOT_EQ, cond, AstNode.makeLeaf(0));
            ifStat.setNode(0, cond);
        }
        // 计算
//        AstNode res = cond.compute();
//        if (res.op == OP.IMMEDIATE) {   // 如何为1或0直接化简
//            int ident = res.getInteger();
//            reduceIf(ifStat, ident == 1);
//            return;
//        }
//        else if (cond.getLeft().op == OP.IMMEDIATE) {   // 处理 1 || a...
//            reduceIf(ifStat, );
//        }
        AstNode nextStat = Utils.findNextStat(ifStat);
//        if (then.isLeaf() || then.getRight().op != OP.GOTO) {
//            then.addNode(AstNode.makeGoTo(nextStat.putLabelIfAbsent(Label::newLabel)));
//        }
        if (el.isLeaf() || el.getRight().op != OP.GOTO) {
            el.addNode(AstNode.makeGoTo(nextStat.putLabelIfAbsent(Label::newLabel)));
        }


        cond.value = bindLabelToStat(then);  // cond.value正好可以用来存储目标标签，方便IF-IR语句的处理

        AstNode newIfElse;
        AstNode newThen, newEl, goTo;
        if (cond.op == OP.OR) {
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
            newThen = AstNode.makeEmptyNode(OP.STATEMENTS);
            goTo = AstNode.makeLeaf(OP.GOTO);
            newThen.addNode(goTo);
            ifStat.setNode(1, newThen);
            //重设原else
            newEl = AstNode.makeEmptyNode(OP.STATEMENTS);
            newEl.addNode(newIfElse);
            // 为newEl增加跳转
//            AstNode nextStat = Utils.findNextStat(ifStat);
//            newEl.addNode(AstNode.makeGoTo(nextStat.putLabelIfAbsent(Label::newLabel)));
            ifStat.setNode(2, newEl);
            // 绑定label
            goTo.value = bindLabelToStat(then);

        } else if (cond.op == OP.AND) {
            // 重设条件
            ifStat.setNode(0, cond.getLeft());
            // 新建子if-else
            newIfElse = AstNode.makeEmptyNode(OP.IF_ELSE);
            // 将“与”右式作为新建if-else的条件
            newIfElse.addNode(cond.getRight());
            // 将then嵌入新建if-else的then语句
            newIfElse.addNode(then);
            // 新建if-else的else语句，创建goto跳转到原else
            newEl = AstNode.makeEmptyNode(OP.STATEMENTS);
            goTo = AstNode.makeLeaf(OP.GOTO);
            newEl.addNode(goTo);
            newIfElse.addNode(newEl);
            // 重设原then
            newThen = AstNode.makeEmptyNode(OP.STATEMENTS);
            newThen.addNode(newIfElse);
            ifStat.setNode(1, newThen);
            // 绑定label
            goTo.value = bindLabelToStat(el);

        } else {
            return;
        }

        resolveIfElse(ifStat);
        resolveIfElse(newIfElse);
    }

    private static void resolveWhile(AstNode whileStat) {
        if (whileStat.op != OP.WHILE) return;

        AstNode nextStat = Utils.findNextStat(whileStat);
        ILabel exit = nextStat.putLabelIfAbsent(Label::newLabel);

        // 将while标签替换为if-else
        whileStat.op = OP.IF_ELSE;
        // 增加false stat
        AstNode el = AstNode.makeEmptyNode(OP.STATEMENTS);
//        el.addNode(AstNode.makeGoTo(exit));
        whileStat.addNode(el);

        AstNode then = whileStat.getNode(1);
        // 往then中加入跳转GOTO
        ILabel whileStatLabel = whileStat.putLabelIfAbsent(Label::newLabel);
        then.addNode(AstNode.makeGoTo(whileStatLabel));
        // then中可能存在continue以及break，将其替换为对应的goto, 特别注意嵌套while

        List<AstNode> continues = Utils.searchNode(whileStat, OP.CONTINUE);
        List<AstNode> breaks = Utils.searchNode(whileStat, OP.BREAK);

        continues.forEach(node -> node.parent.replaceNode(node, AstNode.makeGoTo(whileStatLabel)));
        breaks.forEach(node -> node.parent.replaceNode(node, AstNode.makeGoTo(exit)));
    }

    private static ILabel bindLabelToStat(AstNode statement) {
        // statement的父节点：if-else、while、statement
        if (statement.isLeaf()) {
            statement = Utils.findNextStat(statement);
        } else {
            statement = statement.getLeft();
        }
        assert statement != null;
        return statement.putLabelIfAbsent(Label::newLabel);
    }


}
