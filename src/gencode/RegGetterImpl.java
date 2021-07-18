package gencode;

import genir.IRCode;
import genir.code.InterRepresent;
import symboltable.VarSymbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegGetterImpl implements RegisterGetter {
    private Map<Register, Set<Address>> regDesc = new HashMap<>();
    private Map<Address, Register> varDesc = new HashMap<>();

    private static final String[] GENERAL_REG = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12",};
    private static final String[] SPECIAL_REG = {"R13", "R14", "R15",};


//    public RegGetterImpl(Map<A>) {
//
//    }

    @Override
    public Register getRegister(Address address, InterRepresent ir) {
//        if (irCode instanceof BinocularRepre)
        if (varDesc.containsKey(address)) return varDesc.get(address);

//        for (String name : GENERAL_REG) {
//            Register register = Register.valueOf(name);
//            Set<Address> set = regDesc.getOrDefault(register, null);
//            if (null != set && set.size() == 1 && set.contains(address) && ) {
//
//            }
//        }


        return null;
    }


    public Register getFreeReg() {
        for (Register register : Register.values()) {
            if (isFreeReg(register)) return register;
        }
        return null;
    }

    public boolean isFreeReg(Register register) {
        return !regDesc.containsKey(register) || regDesc.get(register).isEmpty();
    }
}
