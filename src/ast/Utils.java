package ast;

import common.symbol.Variable;

import java.util.List;
import java.util.function.Consumer;

public class Utils {
    public static void interpreterAst(AstNode root) {
        for (AstNode child : root.getSubTrees()) {
            interpreterAst(child);
        }
        System.out.println(root.value.getVal());
    }

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
        if (values.op != OP.VAL_GROUP){
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
        AstValue lVal = left.value;
        AstValue rVal = right.value;
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
            case STATEMENT:
            case VAL_GROUP:
            case ROOT:
            case CONST_DECL:
            case VAR_DECL:
            case CONTINUE:
            case BREAK:
            case FUNC_DECL:
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
    public static void traverse(AstNode root, Consumer<AstNode> handler) {
        handler.accept(root);
        for (AstNode child : root.getSubTrees()) {
            traverse(child, handler);
        }
    }

}
