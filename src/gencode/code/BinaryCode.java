package gencode.code;

import java.util.ArrayList;
import java.util.List;

public class BinaryCode<S, T> extends Code {
    public final String op;
    public final S operand1;
    public final T[] operand2;
    private final boolean alwaysWrap;

//    public BinaryCode(String op, S operand1, List<T> operand2) {
//        this.op = op;
//        this.operand1 = operand1;
//        this.operand2 = new ArrayList<>(operand2);
//    }

    @SafeVarargs
    public BinaryCode(boolean alwaysWrap, String op, S operand1, T... operand2) {
        this.op = op;
        this.alwaysWrap = alwaysWrap;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @SafeVarargs
    public BinaryCode(String op, S operand1, T... operand2) {
        this(true, op, operand1, operand2);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(String.format("%s %s, ", op, operand1));

        if (!alwaysWrap && operand2.length == 1) {
            return sb.append(operand2[0]).toString();
        }
        sb.append('{');
        sb.append(operand2[0]);
        for (int i = 1; i < operand2.length; i++) {
            sb.append(", ").append(operand2[i]);
        }
        sb.append('}');
        return super.toString() + sb;
    }
}
