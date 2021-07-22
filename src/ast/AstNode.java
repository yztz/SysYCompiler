package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AstNode {
    public Value value;
    private List<AstNode> subTree = new ArrayList<>();
    private int idx = -1;

    public void addNode(AstNode node) {
        this.subTree.add(node);
        this.idx ++;
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

    private AstNode(Value value){
        this.value = value;
    }

    public static AstNode makeBinaryNode(Value value, AstNode left, AstNode right) {
        AstNode ret = new AstNode(value);
        ret.addNode(left);
        ret.addNode(right);
        return ret;
    }

    public static AstNode makeUnaryNode(Value value, AstNode subTree) {
        AstNode ret = new AstNode(value);
        ret.addNode(subTree);
        return ret;
    }

    public static AstNode makeEmptyNode(Value value) {
        return new AstNode(value);
    }


    public static AstNode makeLeaf(Value value) {
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
}
