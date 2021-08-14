package compiler.asm;

import java.util.ArrayList;
import java.util.List;

public class AsmSection {
    public AsmSection(/*String label*/) {
        //this.label = label;
    }

    //public String label;
    public List<AsmCode>  statements = new ArrayList<>();

    public void add(String line)
    {
        statements.add(new AsmCode(line));
    }

    public void add(AsmCode code)
    {
        statements.add(code);
    }

    public void add(AsmSection section){
        this.statements.addAll(section.statements);
    }

    public void getText(StringBuilder builder)
    {
        for (AsmCode stmt : statements) {
            builder.append(stmt.toString()).append("\r\n");
        }
    }
}
