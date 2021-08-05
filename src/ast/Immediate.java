package ast;

public class Immediate implements IAstValue {
    public final int value;

    public Immediate(int value) {
        this.value = value;
    }

    public Immediate(String value) {
        this.value = Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return value + "";
    }

}
