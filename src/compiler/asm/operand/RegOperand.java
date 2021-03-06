package compiler.asm.operand;

import compiler.asm.Reg;

public class RegOperand extends Operand {
    public Reg reg;

    public RegOperand(Reg reg) {
        this.reg = reg;
    }

    @Override
    public String getText() {
        return reg.getText();
    }
}
