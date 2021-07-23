package ast;

import ast.symbol.Domain;
import ast.symbol.Variable;

import java.util.List;

public class Utils {
    public static void interpreterAst(AstNode root) {
        for (AstNode child : root.getSubTrees()) {
            interpreterAst(child);
        }
        System.out.println(root.value.getVal());
    }

    public static int assignArray(Variable array, AstNode values, int dimension) {
        if (values.isLeaf() && values.value instanceof Immediate) {
            array.addConstVal(values.getInteger());
            return 1;
        }
        int init_num = 0;
        for (AstNode node : values.getSubTrees()) {
            init_num += assignArray(array, node, dimension - 1);
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
        AstValue value = root.value;
        if (root.isLeaf()) {
            if (value instanceof Immediate) {   // 立即数则返回
                return root;
            } else if (value instanceof Variable) {
                Variable variable = root.getVariable();
                if (variable.isCollapsible()){
                    if (((Variable) value).isArray) {
                        return root;
                    } else {
                        return AstNode.makeLeaf(((Variable) value).indexConstVal(0));
                    }
                } else {
                    return root;
                }
            }
        }
        if (value instanceof OP) {
            AstNode left = calc(root.getLeft());
            AstNode right = calc(root.getRight());
            AstValue lVal = left.value;
            AstValue rVal = right.value;
            switch ((OP) value) {
                case ADD:
                    if (lVal instanceof Immediate && rVal instanceof Immediate)
                        return AstNode.makeLeaf(left.getInteger() + right.getInteger());
                    else
                        return AstNode.makeBinaryNode(value, left, right);
                case MINUS:
                    if (lVal instanceof Immediate)
                        return AstNode.makeLeaf(-left.getInteger());
                    else
                        return AstNode.makeUnaryNode(value, left);
                case SUB:
                    if (lVal instanceof Immediate && rVal instanceof Immediate)
                        return AstNode.makeLeaf(left.getInteger() - right.getInteger());
                    else
                        return AstNode.makeBinaryNode(value, left, right);
                case MUL:
                    if (lVal instanceof Immediate && rVal instanceof Immediate)
                        return AstNode.makeLeaf(left.getInteger() * right.getInteger());
                    else
                        return AstNode.makeBinaryNode(value, left, right);
                case DIV:
                    if (lVal instanceof Immediate && rVal instanceof Immediate)
                        return AstNode.makeLeaf(left.getInteger() / right.getInteger());
                    else
                        return AstNode.makeBinaryNode(value, left, right);
                case MOD:
                    if (lVal instanceof Immediate && rVal instanceof Immediate)
                        return AstNode.makeLeaf(left.getInteger() % right.getInteger());
                    else
                        return AstNode.makeBinaryNode(value, left, right);
                case OFFSET:
                    Variable var = (Variable) lVal;

                    if (var.isCollapsible() && rVal instanceof Immediate) {
                        int offset = right.getInteger();
                        int val = var.indexConstVal(offset);
                        return AstNode.makeLeaf(val);
                    } else {
                        return AstNode.makeBinaryNode(OP.OFFSET, left, right);
                    }
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
//                    System.err.println("无法计算的表达式 [" + value + "]");
//                    System.exit(-1);
                    return root;
            }
        }
//        System.err.println("无法计算的表达式 node class: " + value.getClass().getSimpleName());
        return root;
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
}
