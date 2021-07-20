package gencode.code;

public class TernaryCode<R, S, T> extends Code {
    public final String op;
    public final R operand1;
    public final S operand2;
    public final T operand3;

    public TernaryCode(String op, R operand1, S operand2, T operand3) {
        this.op = op;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("%s %s, %s, %s", op, operand1, operand2, operand3);
    }
}
