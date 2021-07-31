package compiler.asm;

import compiler.genir.IRBlock;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.*;

public class RegGetterImpl extends RegGetter {
    private final Map<Reg, AddressRWInfo> regDesc = new HashMap<>();
    private final Map<AddressRWInfo, Reg> varDesc = new HashMap<>();

    public RegGetterImpl(List<IRBlock> irBlocks) {
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

    /**
     * 这条IR结束后就要释放的
     */
    HashSet<Reg> readyToReleaseReg = new HashSet<>();
    /**
     * 当前IR使用的
     */
    HashSet<Reg> usingRegThisIR = new HashSet<>();

    public void stepToNextIR()
    {
        for (Reg reg : readyToReleaseReg) {
            regDesc.put(reg, null);
        }
        readyToReleaseReg.clear();
        usingRegThisIR.clear();
    }

    /**
     * 获取接下来还要继续使用的寄存器
     */
    @Override
    public List<Reg> getUsingRegNext() {
        List<Reg> regs=new ArrayList<>();
        for (Reg reg : usableRegs) {
            if(isFreeReg(reg) || readyToReleaseReg.contains(reg))
                continue;
            regs.add(reg);
        }

        return regs;
    }
/*    private static final String[] GENERAL_REG = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12",};
    private static final String[] SPECIAL_REG = {"R13", "R14", "R15",};*/

    public Reg getReg(InterRepresent ir, AddressOrData address)
    {
        Map<AddressRWInfo, Reference> refMap = ir.refMap;
        for (AddressRWInfo key : refMap.keySet()) {
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
                readyToReleaseReg.add(register);
                regDesc.put(register, null);
            }else{
                readyToReleaseReg.remove(register);
            }

            usingRegThisIR.add(register);
            return register;
        }

        return null;
    }

    /*public void setReg(InterRepresent ir, AddressOrData address,Reg register) {
        Map<AddressRWInfo, Reference> refMap = ir.refMap;
        for (AddressRWInfo key : refMap.keySet()) {
            if(key.address!=address)
                continue;
            Reference ref = refMap.get(key);

            if(varDesc.containsKey(key))
            {
                Reg reg = varDesc.get(key);
                regDesc.put(reg,null);
            }
            regDesc.put(register, key);
            varDesc.put(key, register);

            if (null == ref.nextRef) {  // 不存在引用则释放reg
                readyToRelease(register);
            }
        }
    }*/

    @Override
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



    protected boolean isFreeReg(Reg register) { //todo 上下两个函数，名字反了
        return (!regDesc.containsKey(register) || regDesc.get(register) == null)
                /*&& !usingRegThisIR.contains(register)*/;
    }

    protected boolean isFreeRegIgnoreCurrentIR(Reg register)
    {
        return (!regDesc.containsKey(register) || regDesc.get(register) == null)
                && !usingRegThisIR.contains(register);
    }

    @Override
    public void releaseAll() {
        regDesc.replaceAll((r, v) -> null);
    }
}
