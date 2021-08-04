package asm.allocator;

import ir.code.IR;

import java.util.Objects;

public class Reference {
    public final IR nextRef;
    public final boolean isAlive;

    public Reference(IR nextRef, boolean isAlive) {
        this.nextRef = nextRef;
        this.isAlive = isAlive;
    }

    @Override
    public String toString() {
        return String.format("nextRef: %s... isAlive: %s", nextRef == null ? "null" : nextRef.op, isAlive);
    }
}
