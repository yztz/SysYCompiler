package common.symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public Domain domain;
    public Map<String, Variable> varTable = new HashMap<>();

    public SymbolTable(Domain domain) {
        this.domain = domain;
    }

    public void addVariable(Variable variable) {
        this.varTable.put(variable.name, variable);
    }

    public Variable searchSymbol(String name) {
        return varTable.getOrDefault(name, null);
    }
}
