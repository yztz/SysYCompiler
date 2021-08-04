package common;

import asm.IAddress;

public enum Register implements IAddress {
    r0,
    r1,
    r2,
    r3,
    r4,
    r5,
    r6,
    r7,
    r8,
    r9,
    r10,
    fp,
    r12,

    sp,
    lr,
    pc;

    public static final Register FP = fp;
    public static final Register SP = sp;
    public static final Register LR = lr;
    public static final Register PC = pc;

}
