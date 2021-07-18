package gencode;

import genir.code.InterRepresent;

public class Ref {
    public final InterRepresent nextRef;
    public final boolean isAlive;

    public Ref(InterRepresent nextRef, boolean isAlive) {
        this.nextRef = nextRef;
        this.isAlive = isAlive;
    }

    @Override
    public String toString() {
        String ref = nextRef == null ? "null" : nextRef.lineNum + "";
        return "nextRef=" + ref + ", isAlive=" + isAlive;
    }
}