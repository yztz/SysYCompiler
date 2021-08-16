package compiler.optim.refgraph;

import compiler.genir.code.InterRepresent;

import java.util.ArrayList;
import java.util.List;

public class RefNode {
    public final List<RefNode> refs = new ArrayList<>();
}
