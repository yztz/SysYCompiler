package asm;

public class Regs {
    public static final Reg R0 =new Reg(0);
    public static final Reg R1 =new Reg(1);
    public static final Reg R2 =new Reg(2);
    public static final Reg R3 =new Reg(3);
    public static final Reg R4 =new Reg(4);
    public static final Reg R5 =new Reg(5);
    public static final Reg R6 =new Reg(6);
    public static final Reg R7 =new Reg(7);
    public static final Reg R8 =new Reg(8);
    public static final Reg R9 =new Reg(9);
    public static final Reg R10 =new Reg(10);
    public static final Reg R11 =new Reg(11);
    public static final Reg R12 =new Reg(12);
    public static final Reg R13 =new Reg(13);
    public static final Reg R14 =new Reg(14);
    public static final Reg R15 =new Reg(15);

    public static final Reg[] REGS = {
            R0,R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15
    };
    public static final Reg FP = R11;
    public static final Reg SP = R13;
    public static final Reg LR = R14;
    public static final Reg PC = R15;

    public Reg getReg(int id)
    {
        return REGS[id];
    }
}
