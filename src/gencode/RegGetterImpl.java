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
    private Map<Address, Register> varDesc = new HashMap<>();

    private static final String[] GENERAL_REG = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12",};
    private static final String[] SPECIAL_REG = {"R13", "R14", "R15",};


    @Override
    public Map<Address, Register> getMapOfIR(InterRepresent ir) {
        Map<Address, Register> ret = new HashMap<>();

        Map<Address, Ref> refMap = ir.refMap;
        for (Address address : refMap.keySet()) {
            Register register;
            Ref ref = ir.refMap.get(address);
            if (!varDesc.containsKey(address)) {    // 临时变量还未映射
                // 获取空寄存器
                register = getFreeReg();
                if (null != register) {
                    regDesc.put(register, address);
                    varDesc.put(address, register);
                } else {
                    System.err.println("寄存器分配失败");
                }
            } else {
                register = varDesc.get(address);
            }

            if (null == ref.nextRef) {  // 不存在引用则释放reg
                Register reg = varDesc.getOrDefault(address, null);
                if (null != reg) {
                    regDesc.put(reg, null);
                }
            }

            ret.put(address, register);
        }

        return ret;
    }

    public Map<Address, Register> getVarDesc() {
        return varDesc;
    }

    public Map<Register, Address> getRegDesc() {
        return regDesc;
    }

    /**
     * 获取临时寄存器，保证在下条IR前释放
     * @return
     */
    public Register getTmpRegister() {
        return getFreeReg();
    }


    private Register getFreeReg() {
        for (Register register : Register.values()) {
            if (isFreeReg(register)) return register;
        }
        return null;
    }

    private boolean isFreeReg(Register register) {
        return !regDesc.containsKey(register) || regDesc.get(register) == null;
    }
}
