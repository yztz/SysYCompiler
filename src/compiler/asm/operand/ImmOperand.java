package compiler.asm.operand;

public class ImmOperand extends Operand {
    public int imm8m;

    public ImmOperand(int imm8m) {
        this.imm8m = imm8m;
    }

    @Override
    public String getText() {
        return String.format("#%d", imm8m);
    }
}
