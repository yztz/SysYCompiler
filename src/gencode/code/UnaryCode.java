package gencode.code;

import java.util.ArrayList;
import java.util.List;

public class UnaryCode<T> extends Code{
    public final T[] operand1;
    public final String op;
    private final boolean alwaysWrap;

    @SafeVarargs
    public UnaryCode(boolean alwaysWrap, String op, T... operand1) {
        this.op = op;
        this.operand1 = operand1;
        this.alwaysWrap = alwaysWrap;
    }

    @SafeVarargs
    public UnaryCode(String op, T... operand1) {
        this(true, op, operand1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append(op).append(' ');
        if (!alwaysWrap && operand1.length == 1) return sb.append(operand1[0]).toString();

        sb.append('{').append(operand1[0]);
        for (int i = 1; i < operand1.length; i++) {
            sb.append(", ").append(operand1[i]);
        }
        sb.append('}');
        return sb.toString();
    }
}
