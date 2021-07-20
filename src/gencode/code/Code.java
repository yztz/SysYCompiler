package gencode.code;

public abstract class Code {
    private String label = "";

    public Code setLabel(String label) {
        this.label = '\n' + label + ":";
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s\n\t", label);
    }
}
