package compiler.asm;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class AsmCode {
    public AsmCode(String template) {
        this.template = template;
    }


    public String template;
    public Supplier<String>[] args;

    public void setArgs(Supplier<String>... args)
    {
        this.args = args;
    }

    @Override
    public String toString() {
        if(args==null)
            return template;
        else
            return String.format(template, Arrays.stream(args).map(Supplier::get));
    }
}
