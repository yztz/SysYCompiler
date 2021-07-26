package ir.code;

public class GoToIR extends IR{
    public Label target;
    public GoToIR(Label target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return String.format("%-4s\t%-7s%-5s", getLabelName(), "goto", target);
    }
}
