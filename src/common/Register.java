package common;

import asm.IAddress;

import java.util.Comparator;

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
    r11,
    fp,
    r12,
    ip,
    r13,
    sp,
    r14,
    lr,
    r15,
    pc;

    public static final Comparator<Register> comparator = Comparator.comparingInt(Enum::ordinal);


}
