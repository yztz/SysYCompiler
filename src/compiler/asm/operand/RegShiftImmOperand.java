package compiler.asm.operand;

import compiler.asm.Reg;

public class RegShiftImmOperand extends Operand {
    public ShiftOp op;
    public Reg regM;
    public int immData;

    public RegShiftImmOperand(ShiftOp op, Reg regM, int immData) {
        this.op = op;
        this.regM = regM;
        this.immData = immData;
    }

    @Override
    public String getText() {
        return String.format("%s, %s #%d",regM.getText(),op.getText(),immData);
    }
}
