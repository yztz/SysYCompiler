package compiler.asm;

import compiler.genir.code.InterRepresent;

public class Reference {
    public final InterRepresent nextRef;
    public final boolean isAlive;

    public Reference(InterRepresent nextRef, boolean isAlive) {
        this.nextRef = nextRef;
        this.isAlive = isAlive;
    }

    @Override
    public String toString() {
        String ref = nextRef == null ? "null" : nextRef.lineNum + "";
        return "nextRef=" + ref + ", isAlive=" + isAlive;
    }
}