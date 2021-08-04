package asm.code;

public class Code {
    public final String content;
    public final boolean isLabel;

    private Code(String content, boolean isLabel){
        this.content = content;
        this.isLabel = isLabel;
    }

    static Code code(String content){
        return new Code(content, false);
    }
    static Code label(String label) {
        return new Code(label, true);
    }

    @Override
    public String toString() {
        return content;
    }
}
