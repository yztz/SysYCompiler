package gencode;

import genir.IRCode;
import symboltable.VarSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegGetterImpl implements RegisterGetter {
    private Map<Register, Set<String>> regDesc = new HashMap<>();
    private Map<VarSymbol, Set<Location>> varDesc = new HashMap<>();

    @Override
    public Register getRegister(IRCode irCode) {
//        if (irCode instanceof BinocularRepre)
        return null;
    }
}
