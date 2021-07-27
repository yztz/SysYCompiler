package ast;

import common.OP;
import common.symbol.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Utils {

    public static int assignConstArray(Variable array, AstNode values, int dimension) {
        if (values.isLeaf() && values.op == OP.IMMEDIATE) {
            array.addConstVal(values.getInteger());
            return 1;
        }
        int init_num = 0;
        for (AstNode node : values.getSubTrees()) {
            init_num += assignConstArray(array, node, dimension - 1);
        }
        array.pos += Math.max(getDimensionSize(array, dimension) - init_num, 0);
        return init_num;
    }

    public static int assignArray(Variable array, AstNode values, int dimension, AstNode group) {
        if (values.op != OP.VAL_GROUP) {
            AstNode left = AstNode.makeLeaf(new OffsetVar(array, array.pos));
            group.addNode(AstNode.makeBinaryNode(OP.ASSIGN, left, values));
            array.pos++;
            return 1;
        }
        int init_num = 0;
        for (AstNode node : values.getSubTrees()) {
            init_num += assignArray(array, node, dimension - 1, group);
        }
        array.pos += Math.max(getDimensionSize(array, dimension) - init_num, 0);
        return init_num;
    }


    public static int getDimensionSize(Variable array, int dimension) {
        List<Integer> dimensions = array.dimensions;
        int size = 1;
        for (int i = dimension + 1; i < dimensions.size(); i++) {
            size *= dimensions.get(i);
        }

        return size;
    }

    public static AstNode calc(AstNode root) {
        OP op = root.op;
        if (root.isLeaf()) {
            if (op == OP.IMMEDIATE) {   // 立即数则返回
                return root;
            } else if (op == OP.VARIABLE) {
                Variable variable = root.getVariable();
                if (variable.isCollapsible()) {
                    if (variable.isArray) {
                        return root;
                    } else {
                        return AstNode.makeLeaf((variable.indexConstVal(0)));
                    }
                }
            }
            return root;
        }

        AstNode left = calc(root.getLeft());
        AstNode right = calc(root.getRight());
        IAstValue lVal = left.value;
        IAstValue rVal = right.value;
        switch (op) {
            case ADD:
                if (lVal instanceof Immediate && rVal instanceof Immediate)
                    return AstNode.makeLeaf(left.getInteger() + right.getInteger());
                else
                    return AstNode.makeBinaryNode(op, left, right);
            case MINUS:
                if (lVal instanceof Immediate)
                    return AstNode.makeLeaf(-left.getInteger());
                else
                    return AstNode.makeUnaryNode(op, left);
            case SUB:
                if (lVal instanceof Immediate && rVal instanceof Immediate)
                    return AstNode.makeLeaf(left.getInteger() - right.getInteger());
                else
                    return AstNode.makeBinaryNode(op, left, right);
            case MUL:
                if (lVal instanceof Immediate && rVal instanceof Immediate)
                    return AstNode.makeLeaf(left.getInteger() * right.getInteger());
                else
                    return AstNode.makeBinaryNode(op, left, right);
            case DIV:
                if (lVal instanceof Immediate && rVal instanceof Immediate)
                    return AstNode.makeLeaf(left.getInteger() / right.getInteger());
                else
                    return AstNode.makeBinaryNode(op, left, right);
            case MOD:
                if (lVal instanceof Immediate && rVal instanceof Immediate)
                    return AstNode.makeLeaf(left.getInteger() % right.getInteger());
                else
                    return AstNode.makeBinaryNode(op, left, right);
            case NEGATE:
                if (lVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() == 0 ? 1 : 0);
                } else {
                    return AstNode.makeUnaryNode(OP.NEGATE, left);
                }
            case GE:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() >= right.getInteger() ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.GE, left, right);
                }
            case GT:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() > right.getInteger() ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.GT, left, right);
                }
            case AND:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() == 1 && right.getInteger() == 1 ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.AND, left, right);
                }
            case OR:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() == 1 || right.getInteger() == 1 ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.OR, left, right);
                }
            case EQ:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() == right.getInteger() ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.EQ, left, right);
                }
            case NOT_EQ:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() != right.getInteger() ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.NOT_EQ, left, right);
                }
            case LE:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() <= right.getInteger() ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.LE, left, right);
                }
            case LT:
                if (lVal instanceof Immediate && rVal instanceof Immediate) {
                    return AstNode.makeLeaf(left.getInteger() < right.getInteger() ? 1 : 0);
                } else {
                    return AstNode.makeBinaryNode(OP.LT, left, right);
                }
            case CALL:
            case PARAM:
            case RETURN:
            case IF_ELSE:
            case WHILE:
            case ASSIGN:
            case STATEMENTS:
            case VAL_GROUP:
            case ROOT:

            case CONTINUE:
            case BREAK:

            default:
                return root;
        }

    }


    public static int getOffset(int[] idx, List<Integer> base) {
        if (idx.length != base.size()) return -1;

        int offset = 0;
        for (int i = 0; i < idx.length; i++) {
            int t = idx[i];
            for (int j = i + 1; j < base.size(); j++) {
                t *= base.get(j);
            }
            offset += t;
        }
        return offset;
    }

    public static AstNode getOffset(AstNode[] idx, List<Integer> base) {
        if (idx.length != base.size()) return null;

        AstNode offset = AstNode.makeLeaf(0);
        for (int i = 0; i < idx.length; i++) {
            AstNode left = idx[i];
            for (int j = i + 1; j < base.size(); j++) {
                AstNode right = AstNode.makeLeaf(base.get(j));
                left = AstNode.makeBinaryNode(OP.MUL, left, right);
            }
            offset = AstNode.makeBinaryNode(OP.ADD, offset, left);
        }
        return offset;
    }

    /**
     * 先序遍历
     */
    public static void preTraverse(AstNode root, Consumer<AstNode> handler) {
        handler.accept(root);
        for (AstNode child : root.getSubTrees()) {
            preTraverse(child, handler);
        }
    }

    /**
     * 后序遍历
     */
    public static void postTraverse(AstNode root, Consumer<AstNode> handler) {
        for (AstNode child : root.getSubTrees()) {
            postTraverse(child, handler);
        }
        handler.accept(root);
    }


    public static List<AstNode> searchNode(AstNode root, OP op) {
        List<AstNode> ret = new ArrayList<>();
        Utils.postTraverse(root, node -> {
            if (node.op == op) ret.add(node);
        });
        return ret;
    }

    public static AstNode findFirstStat(AstNode currStat) {
        while (!OP.isStatement(currStat)) {
            currStat = currStat.getLeft();
        }

        return currStat;
    }

    public static AstNode findNextStat(AstNode currStat) {
//        if (currStat.parent.getRight() != currStat) return currStat.parent.getRight(currStat);
        // 当父节点不为statement或者当前节点为最右节点
        while (currStat.parent.op != OP.STATEMENTS
                || currStat == currStat.parent.getRight())
            currStat = currStat.parent;
        currStat = currStat.parent.getRight(currStat);
        // 当前节点不是语句
        while (!OP.isStatement(currStat)) {
            // 当前节点是叶节点，右移寻找非叶节点
            if (currStat.isLeaf()) {
                while (currStat.isLeaf()) {
                    AstNode right = currStat.parent.getRight(currStat);
                    if (null == right) {
                        System.err.println("不存在下条可用的语句");
                        return null;
                    } else {
                        currStat = right;
                    }
                }
                continue;
            }
            currStat = currStat.getLeft();
        }
        return currStat;
    }




}
