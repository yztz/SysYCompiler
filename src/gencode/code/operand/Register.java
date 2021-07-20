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
    fp,    // FP
    ip,    // IP

    /* SP */
    sp,
    /* LR */
    lr,
    /* PC */
    pc
}
