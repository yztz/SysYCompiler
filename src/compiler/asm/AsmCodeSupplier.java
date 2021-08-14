package compiler.asm;

import java.util.function.Consumer;

public class AsmCodeSupplier extends AsmCode{
    Consumer<AsmBuilder> generator;

    public AsmCodeSupplier(Consumer<AsmBuilder> generator) {
        this.generator = generator;
    }

    @Override
    public String getAsmText() {
        AsmBuilder builder = new AsmBuilder();
        generator.accept(builder);
        StringBuilder sb=new StringBuilder();
        builder.getSection().getText(sb);
        return sb.toString();
    }
}
