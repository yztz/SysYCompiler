package ir.code;

public class Label{
    private static int next_id = 0;

    public final String name;
    public IR bindIR;
//    public IRHolder irHolder = new IRHolder();

    public void bindIR(IR ir) {
        this.bindIR = ir;
    }

    public IR getIR() {
        return this.bindIR;
    }

    private Label(String name) {
        this.name = name;
    }

    public static Label newLabel() {
        return new Label("L" + next_id++);
    }
    public static Label newLabel(String name) {
        return new Label(name);
    }

    public static Label newLabel(IR ir) {
        Label label = newLabel();
        label.bindIR = ir;
        return label;
    }


    @Override
    public String toString() {
        return name;
    }
}
