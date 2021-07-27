package ast;

import common.ILabel;
import common.OP;
import common.symbol.Function;
import common.symbol.Variable;
import org.antlr.v4.runtime.tree.Tree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class AstNode implements Tree {
    public AstNode parent;
    public OP op;
    public IAstValue value;
    public ILabel label;

    private final List<AstNode> subTree = new LinkedList<>();

    public void setNode(int idx, AstNode node) {
        if (null == node) return;
        node.parent = this;
        subTree.set(idx, node);
    }

    public void replaceNode(AstNode old, AstNode niu) {
        int idx = subTree.indexOf(old);
        subTree.set(idx, niu);
        niu.parent = this;
        old.parent = null;
    }

    public void removeNode(AstNode node) {
        if (null == node) return;

        subTree.remove(node);
        node.parent = null;
    }

    public void addNode(AstNode node) {
        if (null == node) return;

        this.subTree.add(node);
        node.parent = this;
    }

    public void addNode(List<AstNode> nodes) {
        this.subTree.addAll(nodes);
        for (AstNode node : nodes) {
            if (null == node) continue;
            node.parent = this;
        }
    }

    public void insertAfter(AstNode refNode, AstNode newNode) {
        int idx = subTree.indexOf(refNode);
        insert(idx + 1, newNode);
    }

    public void insertBefore(AstNode refNode, AstNode newNode) {
        int idx = subTree.indexOf(refNode);
        insert(idx, newNode);
    }

    public void insert(int idx, AstNode newNode) {
        subTree.add(idx, newNode);
        newNode.parent = this;
    }

    public AstNode getRight() {
        return subTree.get(subTree.size() - 1);
    }

    public AstNode getRight(AstNode node) {
        int idx = subTree.indexOf(node);
        if (idx == subTree.size() - 1)
            return null;
        else
            return subTree.get(idx + 1);
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

    public ILabel putLabelIfAbsent(Supplier<ILabel> supplier) {
        if (label == null) {
            label = supplier.get();
        }
        return label;
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

    private String getLabelName() {
        return null == label ? "" : label.getLabelName() + ":";
    }

    private AstNode(OP op) {
        this.op = op;
    }

    private AstNode(OP op, IAstValue value) {
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

    public static AstNode makeGoTo(ILabel value) {
        return new AstNode(OP.GOTO, value);
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
            return value.toString();
        else
            return op.name();
    }

    @Override
    public Tree getParent() {
        return parent;
    }

    @Override
    public Object getPayload() {
        switch (op) {
            case IMMEDIATE:
            case VARIABLE:
            case VAR_OFFSET:
                return value;
        }
        if (null == value)
            return String.format("%s%s", getLabelName(), op);
        else
            return String.format("%s%s[%s]", getLabelName(),op, value);
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
