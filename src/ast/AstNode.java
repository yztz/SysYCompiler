package ast;

import common.symbol.Function;
import common.symbol.Variable;
import ir.code.Label;
import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AstNode implements Tree {
    public AstNode parent;
    public OP op;
    public AstValue value;

    private List<AstNode> subTree = new LinkedList<>();
    private int idx = -1;

    public void setNode(int idx, AstNode node) {
        if (null == node) return;
        node.parent = this;
        subTree.set(idx, node);
    }

    public void removeNode(AstNode node) {
        boolean exist = subTree.remove(node);
        if (exist) idx--;
    }

    public void addNode(AstNode node) {
        if (null == node) return;

        this.subTree.add(node);
        node.parent = this;
        this.idx++;
    }

    public void addNode(List<AstNode> nodes) {
        this.subTree.addAll(nodes);
        for (AstNode node : nodes) {
            if (null == node) continue;
            node.parent = this;
        }
        this.idx += nodes.size();
    }

    public AstNode getRight() {
        return subTree.get(idx);
    }

    public AstNode getLeft() {
        return subTree.get(0);
    }

    public List<AstNode> getSubTrees() {
        return Collections.unmodifiableList(subTree);
    }

    public boolean isLeaf() {
        return this.subTree.size() == 0;
    }

    public AstNode getNode(int i) {
        if (i > getChildrenNum() - 1) return null;
        return subTree.get(i);
    }

    public int getChildrenNum() {
        return subTree.size();
    }

    public int count() {
        int ret = 1;
        for (AstNode child : subTree) {
            ret += child.count();
        }
        return ret;
    }

    public int getInteger() {
        if (value instanceof Immediate) {
            return ((Immediate) value).value;
        } else {
            System.err.println("expect an Immediate, but found [" + value.getClass().getSimpleName() + "]");
            return -1;
        }
    }

    public Variable getVariable() {
        if (value instanceof Variable) {
            return (Variable) value;
        } else {
            System.err.println("expect an Variable, but found [" + value.getClass().getSimpleName() + "]");
            return null;
        }
    }

    private AstNode(OP op) {
        this.op = op;
    }

    private AstNode(OP op, AstValue value) {
        this.op = op;
        this.value = value;
    }

    public static AstNode makeBinaryNode(OP op, AstNode left, AstNode right) {
        AstNode ret = new AstNode(op);
        ret.addNode(left);
        ret.addNode(right);
        return ret;
    }

    public static AstNode makeUnaryNode(OP op, AstNode subTree) {
        AstNode ret = new AstNode(op);
        ret.addNode(subTree);
        return ret;
    }

    public static AstNode makeEmptyNode(OP op) {
        return new AstNode(op);
    }
//
//    public static AstNode makeLeaf(Label value) {
//        return new AstNode(OP.LABEL, value);
//    }

    public static AstNode makeLeaf(Variable value) {
        return new AstNode(OP.VARIABLE, value);
    }

    public static AstNode makeLeaf(OffsetVar var) {
        return new AstNode(OP.VAR_OFFSET, var);
    }

    public static AstNode makeLeaf(Function value) {
        return new AstNode(OP.FUNCTION, value);
    }

    public static AstNode makeLeaf(OP op) {
        return new AstNode(op);
    }

    public static AstNode makeLeaf(int value) {
        return new AstNode(OP.IMMEDIATE, new Immediate(value));
    }

    public static AstNode makeLeaf(String value) {
        return makeLeaf(Integer.parseInt(value));
    }



    @Override
    public String toString() {
        if (value != null)
            return value.getVal();
        else
            return op.name();
    }

    @Override
    public Tree getParent() {
        return parent;
    }

    @Override
    public Object getPayload() {
        if (null == value)
            return op;
        else
            return value;
    }

    @Override
    public Tree getChild(int i) {
        return getNode(i);
    }

    @Override
    public int getChildCount() {
        return getChildrenNum();
    }

    @Override
    public String toStringTree() {
        return null;
    }
}
