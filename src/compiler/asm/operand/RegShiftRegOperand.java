package compiler.asm.operand;

import compiler.asm.Reg;

public class RegShiftRegOperand extends Operand{
    public ShiftOp op;
    public Reg regM;
    public Reg regS;

    public RegShiftRegOperand(ShiftOp op, Reg regM, Reg regS) {
        this.op = op;
        this.regM = regM;
        this.regS = regS;
    }

    @Override
    public String getText() {
        return String.format("%s, %s %s",regM.getText(),op.getText(),regS.getText());
    }
}
