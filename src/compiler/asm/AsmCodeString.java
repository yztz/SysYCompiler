package compiler.asm;

public class AsmCodeString extends AsmCode {
    String template;
    public AsmCodeString(String template) {
        this.template = template;
    }

    @Override
    public String getAsmText() {
        return template+"\r\n";
    }
}
