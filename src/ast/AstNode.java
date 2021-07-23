package ast;

import ast.symbol.Variable;
import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AstNode implements Tree {
    public AstNode parent;
    public AstValue value;
    private List<AstNode> subTree = new ArrayList<>();
    private int idx = -1;

    public void addNode(AstNode node) {
        if (null == node) return;

        this.subTree.add(node);
        node.parent = this;
        this.idx ++;
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
        return subTree.get(i);
    }

    public int getChildrenNum() {
        return subTree.size();
    }

    public int getInteger() {
        if (value instanceof Immediate) {
            return ((Immediate) value).value;
        } else {
            System.err.println("expect an Immediate, but found [" + value.getClass().getSimpleName()+"]");
            return -1;
        }
    }

    public Variable getVariable() {
        if (value instanceof Variable) {
            return (Variable) value;
        } else {
            System.err.println("expect an Variable, but found [" + value.getClass().getSimpleName()+"]");
            return null;
        }
    }

    private AstNode(AstValue value){
        this.value = value;
    }

    public static AstNode makeBinaryNode(AstValue value, AstNode left, AstNode right) {
        AstNode ret = new AstNode(value);
        ret.addNode(left);
        ret.addNode(right);
        return ret;
    }

    public static AstNode makeUnaryNode(AstValue value, AstNode subTree) {
        AstNode ret = new AstNode(value);
        ret.addNode(subTree);
        return ret;
    }

    public static AstNode makeEmptyNode(AstValue value) {
        return new AstNode(value);
    }


    public static AstNode makeLeaf(AstValue value) {
        return new AstNode(value);
    }

    public static AstNode makeLeaf(int value) {
        return makeLeaf(new Immediate(value));
    }

    public static AstNode makeLeaf(String value) {
        return makeLeaf(Integer.parseInt(value));
    }

    @Override
    public String toString() {
        return value.getVal();
    }

    @Override
    public Tree getParent() {
        return parent;
    }

    @Override
    public Object getPayload() {
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
