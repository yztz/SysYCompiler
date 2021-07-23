package asm;

import java.util.ArrayList;
import java.util.List;

public class AsmSection {
    public AsmSection(/*String label*/) {
        //this.label = label;
    }

    //public String label;
    public List<String>  statements = new ArrayList<>();

    public void add(String line)
    {
        statements.add(line);
    }
}
