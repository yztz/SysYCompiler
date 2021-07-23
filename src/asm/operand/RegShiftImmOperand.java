package asm.operand;

import asm.Reg;

public class RegShiftImmOperand extends Operand {
    public ShiftOp op;
    public Reg regM;
    public int immData;


    @Override
    public String getText() {
        return String.format("%s, %s #%d",regM.getText(),op.getText(),immData);
    }
}
