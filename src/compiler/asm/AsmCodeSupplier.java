package compiler.asm;

import java.util.function.Consumer;

public class AsmCodeSupplier extends AsmCode{
    Consumer<AsmBuilder> generator;
    FunctionDataHolder dataHolder;
    RegGetter regGetter;

    public AsmCodeSupplier(Consumer<AsmBuilder> generator) {
        this.generator = generator;
    }

    public AsmCodeSupplier(Consumer<AsmBuilder> generator, FunctionDataHolder dataHolder, RegGetter regGetter) {
        this.generator = generator;
        this.dataHolder = dataHolder;
        this.regGetter = regGetter;
    }

    @Override
    public String getAsmText() {
        AsmBuilder builder = new AsmBuilder();
        if(dataHolder!=null && regGetter!=null)
            builder.hookIfNotImmXX(dataHolder,regGetter);
        generator.accept(builder);
        StringBuilder sb=new StringBuilder();
        builder.getSection().getText(sb);
        return sb.toString();
    }
}
