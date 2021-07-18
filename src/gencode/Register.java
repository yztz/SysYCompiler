package gencode;

public enum Register implements Location {
    /* 通用 */
    R0,
    R1,
    R2,
    R3,
    R4,
    R5,
    R6,
    R7,
    R8,
    R9,
    R10,
    R11,    // FP
    R12,    // IP

    /* SP */
    R13,
    /* LR */
    R14,
    /* PC */
    R15;
}
