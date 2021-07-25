package compiler.asm;

import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RegGetterImpl implements RegGetter {
    private final Map<Reg, Address> regDesc = new HashMap<>();
    private final Map<Address, Reg> varDesc = new HashMap<>();


/*    private static final String[] GENERAL_REG = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12",};
    private static final String[] SPECIAL_REG = {"R13", "R14", "R15",};*/

    public Reg getRegOfAddress(InterRepresent ir, AddressOrData address)
    {
        Map<Address, Reference> refMap = ir.refMap;
        for (Address key : refMap.keySet()) {
            if(key.address!=address)
                continue;
            Reference ref = refMap.get(key);
            Reg register;

            if (!varDesc.containsKey(key)) {    // 临时变量还未映射
                // 获取空寄存器
                register = getFreeReg();
                if (null != register) {
                    regDesc.put(register, key);
                    varDesc.put(key, register);
                } else {
                    System.err.println("寄存器分配失败");
                }
            } else {
                register = varDesc.get(key);
            }

            if (null == ref.nextRef) {  // 不存在引用则释放reg
                Reg reg = varDesc.getOrDefault(key, null);
                if (null != reg) {
                    regDesc.put(reg, null);
                }
            }
            return register;
        }

        return null;
    }

    @Override
    public Map<Address, Reg> getMapOfIR(InterRepresent ir) {
        Map<Address, Reg> ret = new HashMap<>();

        Map<Address, Reference> refMap = ir.refMap;
        for (Address address : refMap.keySet()) {
            Reg register;
            Reference reference = ir.refMap.get(address);
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

            if (null == reference.nextRef) {  // 不存在引用则释放reg
                Reg reg = varDesc.getOrDefault(address, null);
                if (null != reg) {
                    regDesc.put(reg, null);
                }
            }

            ret.put(address, register);
        }

        return ret;
    }

    public Map<Address, Reg> getVarDesc() {
        return varDesc;
    }

    public Map<Reg, Address> getRegDesc() {
        return regDesc;
    }

    /**
     * 获取临时寄存器，保证在下条IR前释放
     */
    public Reg getTmpRegister(int index) {
        return getFreeReg(index);
    }

    @Override
    public Reg getTmpRegister(Function<Reg, Boolean> filter, int index) {
        int i = 0;
        for (Reg register : Regs.REGS) {
            if (isFreeReg(register) & filter.apply(register)){
                if(i==index)
                    return register;
                i++;
            }
        }
        return null;
    }

    /**
     * 获取临时寄存器，保证在下条IR前释放
     */
    public Reg getTmpRegister() {
        return getFreeReg(0);
    }

    /**
     * 获取空闲的寄存器
     * @param index 第几个空闲的寄存器
     */
    private Reg getFreeReg(int index) {
        int i = 0;
        for (Reg register : Regs.REGS) {
            if (isFreeReg(register)){
                if(i==index)
                    return register;
                i++;
            }
        }
        return null;
    }

    private Reg getFreeReg() {
        return getFreeReg(0);
        /*for (Reg register : Regs.REGS) {
            if (isFreeReg(register)) return register;
        }
        return null;*/
    }

    private boolean isFreeReg(Reg register) {
        return !regDesc.containsKey(register) || regDesc.get(register) == null;
    }
}
