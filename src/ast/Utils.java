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
            array.value[array.pos] = ((Immediate) values.value).value;
            array.pos++;
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

    public static int calc(AstNode root) {
        AstValue value = root.value;
        if (root.isLeaf()) {
            if (value instanceof Immediate) {   // 立即数则返回值
                return ((Immediate) value).value;
            } else if (value instanceof Variable) {
                if (((Variable) value).isConst) {   // 常量值
                    return ((Variable) value).value[0];
                } else {
                    System.err.println("A constant val expected");
                    return -1;
                }
            }
        }
        if (value instanceof OP) {
            switch ((OP) value) {
                case ADD:
                    return calc(root.getLeft()) + calc(root.getRight());
                case MINUS:
                    return -calc(root.getLeft());
                case SUB:
                    return calc(root.getLeft()) - calc(root.getRight());
                case MUL:
                    return calc(root.getLeft()) * calc(root.getRight());
                case DIV:
                    return calc(root.getLeft()) / calc(root.getRight());
                case MOD:
                    return calc(root.getLeft()) % calc(root.getRight());
                case OFFSET:
                    AstNode left = root.getLeft();
                    AstNode offset = root.getRight();
                    Variable var = (Variable) left.value;
                    return var.value[calc(offset)];
                default:
                    System.err.println("无法计算的表达式");
                    return -1;
            }
        } else {
            System.err.println("无法计算的表达式");
            return -1;
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
}
