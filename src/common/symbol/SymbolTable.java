package common.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    public Domain domain;
    public Map<String, Variable> varTable = new HashMap<>();

    public List<Variable> constVariable = new ArrayList<>();
    public List<Variable> normalVariable = new ArrayList<>();

    public SymbolTable(Domain domain) {
        this.domain = domain;
    }

    public void addVariable(Variable variable) {
        if (variable.isConst)
            constVariable.add(variable);
        else
            normalVariable.add(variable);

        this.varTable.put(variable.name, variable);
    }

    public Variable searchSymbol(String name) {
        return varTable.getOrDefault(name, null);
    }
}
