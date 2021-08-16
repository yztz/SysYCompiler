package compiler.optim.refgraph;

import compiler.genir.code.InterRepresent;

import java.util.Objects;

public class IrRefNode extends RefNode{
    public InterRepresent ir;

    public IrRefNode(InterRepresent ir) {
        this.ir = ir;
    }

    @Override
    public String toString() {
        return "IrRefNode{" + "ir=" + ir + ", refs=" + refs + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IrRefNode irRefNode = (IrRefNode) o;
        return ir.equals(irRefNode.ir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ir);
    }
}
