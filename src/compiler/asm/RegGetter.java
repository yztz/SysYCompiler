package compiler.asm;

import compiler.genir.IRBlock;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.*;
import java.util.function.Function;

public class RegGetter {
    private final Map<Reg, AddressRWInfo> regDesc = new HashMap<>();
    private final Map<AddressRWInfo, Reg> varDesc = new HashMap<>();
    protected Reg[] usableRegs = {Regs.R4, Regs.R5, Regs.R6, Regs.R7, Regs.R8, Regs.R9, Regs.R10, Regs.R12, Regs.R0, Regs.R1, Regs.R2, Regs.R3};
    /**
     * 这条IR结束后就要释放的
     */
    HashSet<Reg> readyToReleaseReg = new HashSet<>();
    /**
     * 当前IR使用的
     */
    HashSet<Reg> usingRegThisIR = new HashSet<>();
    public RegGetter(List<IRBlock> irBlocks) {
        calNextRef(irBlocks);
    }

    /**
     * 计算变量的下次引用与活跃度
     */
    public void calNextRef(List<IRBlock> irBlocks) {
        for (IRBlock block : irBlocks) {
            Map<AddressRWInfo, Reference> refTable = new HashMap<>();
            for (int i = block.irs.size() - 1; i >= 0; i--) {
                InterRepresent ir = block.irs.get(i);

                ir.getAllAddressRWInfo().forEach(var -> {
                    Reference ref = refTable.getOrDefault(var, new Reference(null, true));
                    // a & c
                    ir.refMap.put(var, ref);
                    if (var.isWrite) {
                        // b
                        refTable.put(var, new Reference(null, false));
                    } else {
                        // d
                        refTable.put(var, new Reference(ir, true));
                    }
                });
            }
        }
    }

    public void stepToNextIR() {
        for (Reg reg : readyToReleaseReg) {
            regDesc.put(reg, null);
        }
        readyToReleaseReg.clear();
        usingRegThisIR.clear();
    }

    /**
     * 获取接下来还要继续使用的寄存器
     */
    public List<Reg> getUsingRegNext() {
        List<Reg> regs = new ArrayList<>();
        for (Reg reg : usableRegs) {
            if (isFreeReg(reg) || readyToReleaseReg.contains(reg)) continue;
            regs.add(reg);
        }

        return regs;
    }


    public Reg getReg(InterRepresent ir, AddressOrData address) {
        Map<AddressRWInfo, Reference> refMap = ir.refMap;
        for (AddressRWInfo key : refMap.keySet()) {
            if (key.address != address) continue;
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
                readyToReleaseReg.add(register);
                regDesc.put(register, null);
            } else {
                readyToReleaseReg.remove(register);
            }

            usingRegThisIR.add(register);
            return register;
        }

        return null;
    }

    public Map<AddressRWInfo, Reg> getMapOfIR(InterRepresent ir) {
        Map<AddressRWInfo, Reg> ret = new HashMap<>();

        Map<AddressRWInfo, Reference> refMap = ir.refMap;
        for (AddressRWInfo addressRWInfo : refMap.keySet()) {
            Reg register;
            Reference reference = ir.refMap.get(addressRWInfo);
            if (!varDesc.containsKey(addressRWInfo)) {    // 临时变量还未映射
                // 获取空寄存器
                register = getFreeReg();
                if (null != register) {
                    regDesc.put(register, addressRWInfo);
                    varDesc.put(addressRWInfo, register);
                } else {
                    System.err.println("寄存器分配失败");
                }
            } else {
                register = varDesc.get(addressRWInfo);
            }

            if (null == reference.nextRef) {  // 不存在引用则释放reg
                Reg reg = varDesc.getOrDefault(addressRWInfo, null);
                if (null != reg) {
                    regDesc.put(reg, null);
                }
            }

            ret.put(addressRWInfo, register);
        }

        return ret;
    }

    public Map<AddressRWInfo, Reg> getVarDesc() {
        return varDesc;
    }

    public Map<Reg, AddressRWInfo> getRegDesc() {
        return regDesc;
    }


    public boolean requireReg(Reg reg)
    {
        if(readyToReleaseReg.contains(reg))
            return true;

        usingRegThisIR.add(reg);
        return true;
    }

    /**
     * 获取临时寄存器，保证在下条IR前释放
     */
    public Reg getTmpRegister() {
        return getTmpRegister(r->true,0);
    }
    /**
     * 获取临时寄存器，保证在下条IR前释放
     */
    public Reg getTmpRegister(int index) { //todo index参数是不必要的
        return getTmpRegister(r->true,0);
    }

    public Reg getTmpRegister(Function<Reg, Boolean> filter, int index) {
        int i = 0;
        for (Reg register : usableRegs) {
            if (isFreeRegIgnoreCurrentIR(register) && filter.apply(register)) {
                if (i == index) {
                    usingRegThisIR.add(register);
                    readyToReleaseReg.add(register);
                    return register;
                }
                i++;
            }
        }
        return null;
    }

    public void releaseAll() {
        regDesc.replaceAll((r, v) -> null);
    }

    public List<Reg> getUsingReg() {
        List<Reg> regs = new ArrayList<>();
        for (int i = 0; i < usableRegs.length; i++) {
            if (!isFreeReg(usableRegs[i])) regs.add(usableRegs[i]);
        }

        return regs;
    }

    /**
     * 获取空闲的寄存器
     *
     * @param index 第几个空闲的寄存器
     */
    private Reg getFreeReg(int index) {
        int i = 0;
        for (Reg register : usableRegs) {
            if (isFreeRegIgnoreCurrentIR(register)) {
                if (i == index) return register;
                i++;
            }
        }
        return null;
    }

    /**
     * 确定寄存器之后不会用到之后可以手动释放
     * @param reg 要释放的寄存器
     */
    public void releaseReg(Reg reg)
    {
        regDesc.put(reg,null);
        readyToReleaseReg.remove(reg);
        usingRegThisIR.remove(reg);
    }

    /**
     * 在usingRegThisIR中的寄存器不会再被分配
     * 如果该寄存器在下条IR前将要被释放，那么可以提前释放，获得更多可用寄存器
     * @param reg
     */
    public void releaseRegIfNoRef(Reg reg)
    {
        if(readyToReleaseReg.contains(reg))
            usingRegThisIR.remove(reg);
    }

    /**
     * 获取第一个空闲的寄存器
     * @return 寄存器
     */
    protected Reg getFreeReg() {
        return getFreeReg(0);
    }

    protected boolean isFreeReg(Reg register) { //todo 上下两个函数，名字反了
        return (!regDesc.containsKey(register) || regDesc.get(register) == null)
                /*&& !usingRegThisIR.contains(register)*/;
    }

    protected boolean isFreeRegIgnoreCurrentIR(Reg register) {
        return (!regDesc.containsKey(register) || regDesc.get(register) == null) && !usingRegThisIR.contains(register);
    }
}
