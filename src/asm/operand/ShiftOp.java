package asm.operand;

public enum ShiftOp {
    LSL,
    LSR,
    ASR,
    ROR,
    RRX;

    public String getText()
    {
        return this.name();
    }
}
