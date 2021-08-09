package asm.allocator;

import asm.IAddress;
import asm.IName;
import common.OffsetVar;
import common.Register;
import common.symbol.Variable;
import ir.code.IR;

import java.util.*;

import static common.Register.*;
import static common.Register.r12;

public class Describer {
    private static final IAddress MEM_ADDR = new MEMAddr();

    public static final Register[] availableReg = {
            r4, r5, r6, r7, r8, r9, r10
    };

    private Set<Register> locked = new HashSet<>();

    private Map<Register, Set<IName>> regDesc = new HashMap<>();
    private Map<IName, Set<IAddress>> addrDesc = new HashMap<>();

    /* 记录被主动申请的寄存器 */
    public void unlock() {
        locked.clear();
    }

    private void initName(IName name) {
        Set<IAddress> set = new HashSet<>();
        set.add(MEM_ADDR);
        addrDesc.put(name, set);
    }

    public Describer(BasicBlock block) {
        for (IR ir : block.getIRs()) {
            ir.getNames().forEach(name -> {
                if (name instanceof Variable) {
                    initName((name));
                } else if (name instanceof OffsetVar) {
                    OffsetVar offsetVar = ((OffsetVar) name);
                    initName(offsetVar.variable);
                    initName(offsetVar);
                    if (offsetVar.getOffset() instanceof Variable) initName(((Variable) offsetVar.getOffset()));
                } else {
                    Set<IAddress> set = new HashSet<>();
                    addrDesc.put(name, set);
                }
            });
        }
        for (Register register : availableReg) {
            regDesc.put(register, new HashSet<>());
        }
    }

    public Set<IName> getAllNames() {
        return addrDesc.keySet();
    }

    /*
        从地址描述符中删除名字的内存地址
     */
    public void removeMem(IName name) {
        addrDesc.get(name).remove(MEM_ADDR);
    }

    /*
        获取第一个含有名字的寄存器
     */
    public Register getRegister(IName name) {
        for (Register register : availableReg) {
            if (regDesc.get(register).contains(name)) return register;
        }
        return null;
    }

    public void removeAddr(IName name, IAddress address) {
        addrDesc.get(name).remove(address);
    }

    /*
        释放寄存器
     */
    public void freeReg(Register register) {
        regDesc.get(register).forEach(name -> {
            addrDesc.get(name).remove(register);
        });
        regDesc.get(register).clear();
    }

    public void lock(Register register) {
        this.locked.add(register);
    }

    /*
        将名字映射到寄存器上
     */
    public void loadName(Register register, IName name) {
        for (IName tmp : regDesc.get(register)) {
            addrDesc.get(tmp).remove(register);
        }
        regDesc.get(register).clear();
        regDesc.get(register).add(name);
        addrDesc.get(name).add(register);
    }

    /*
        更新名字
     */
    public void updateName(Register rx, IName name) {
        for (IName tmp : regDesc.get(rx)) {
            addrDesc.get(tmp).remove(rx);
        }
        regDesc.get(rx).clear();
        regDesc.get(rx).add(name);
        addrDesc.get(name).clear();
        addrDesc.get(name).add(rx);
    }

    /*
        保存名字
     */
    public void freeName(IName name) {
        for (Register register : availableReg) {
            regDesc.get(register).remove(name);
        }
        addrDesc.get(name).clear();
        addrDesc.get(name).add(MEM_ADDR);
    }

    /*
        确认名字保存在内存中
     */
    public boolean isInMemory(IName name) {
        for (IAddress address : addrDesc.get(name)) {
            if (address == MEM_ADDR) return true;
        }
        return false;
    }

    public Set<Register> getRegisters(IName name) {
        Set<IAddress> addresses = addrDesc.get(name);
        Set<Register> registers = new HashSet<>();
        for (IAddress addr : addresses) {
            if (addr instanceof Register) registers.add(((Register) addr));
        }
        return registers;
    }

    public void copyName(Register target, IName name) {
        regDesc.get(target).add(name);
        addrDesc.get(name).clear();
        addrDesc.get(name).add(target);
    }

    public boolean isLocked(Register register) {
        return locked.contains(register);
    }

    public Register getFreeRegister() {
        for (Register register : availableReg) {
            if (isRegEmpty(register) && !locked.contains(register)) {
                locked.add(register);
                return register;
            }
        }

        return null;
    }

    public boolean isRegEmpty(Register register) {
        return regDesc.get(register).isEmpty();
    }

    public Set<IName> getNames(Register register) {
        return new HashSet<>(regDesc.get(register));
    }


}
