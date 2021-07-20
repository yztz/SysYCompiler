package gencode.code.operand;

import gencode.code.interfaces.Location;
import gencode.code.interfaces.RegOrImm;

public enum Register implements RegOrImm {
    /* 通用 */
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
    r11,    // FP
    r12,    // IP

    /* SP */
    r13,
    /* LR */
    r14,
    /* PC */
    r15;
}
