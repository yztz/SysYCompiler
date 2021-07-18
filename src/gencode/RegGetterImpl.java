package gencode;

import genir.IRCode;
import genir.code.InterRepresent;
import symboltable.VarSymbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegGetterImpl implements RegisterGetter {
    private Map<Register, Address> regDesc = new HashMap<>();


    private static final String[] GENERAL_REG = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12",};
    private static final String[] SPECIAL_REG = {"R13", "R14", "R15",};


//    public RegGetterImpl(Map<A>) {
//
//    }

    @Override
    public Map<Address, Register> getRegister(IRCode irCode) {
        Map<Address, Register> varDesc = new HashMap<>();
        for (InterRepresent ir : irCode.codes) {
            if (null == ir.refMap) continue;
            for (Address address : ir.refMap.keySet()) {
                Ref ref = ir.refMap.get(address);
                if (!varDesc.containsKey(address)) {    // 临时变量还未映射
                    Register register = getFreeReg();
                    if (null != register) {
                        regDesc.put(register, address);
                        varDesc.put(address, register);
                    } else {
                        System.err.println("寄存器分配失败");
                    }
                }
                if (null == ref.nextRef) {  // 不存在引用则释放reg
                    Register reg = varDesc.getOrDefault(address, null);
                    if (null != reg) {
                        regDesc.put(reg, null);
                    }
                }
            }
        }
        return varDesc;
    }


    public Register getFreeReg() {
        for (Register register : Register.values()) {
            if (isFreeReg(register)) return register;
        }
        return null;
    }

    public boolean isFreeReg(Register register) {
        return !regDesc.containsKey(register) || regDesc.get(register) == null;
    }
}
