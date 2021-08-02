package compiler.asm;

import compiler.ConstDef;
import compiler.genir.IRBlock;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.*;
import java.util.function.Function;

public class RegGetter {
    private final Map<Reg, AddressRWInfo> regDesc = new HashMap<>();
    private final Map<AddressRWInfo, RegOrMem> varDesc = new HashMap<>();
    protected Reg[] usableRegs = {Regs.R4, Regs.R5, Regs.R6, Regs.R7, Regs.R8, Regs.R9, Regs.R10/*, Regs.R12*//*, Regs.R0, Regs.R1, Regs.R2, Regs.R3*/};

    private boolean hookIfNotEnough;
    private AsmBuilder builder;
    //private FunctionDataHolder dataHolder;
    public void hookIfNotEnough(AsmBuilder builder/*,FunctionDataHolder holder*/) {
        this.hookIfNotEnough = true;
        this.builder= builder;
        //this.dataHolder = holder;
    }

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
    private Map<AddressOrData,Integer> refTimes=new HashMap<>();

    private static final int regStagingMemSize = FunctionDataHolder.RegFuncData.size;
    private boolean[] usedRegStagingMem = new boolean[regStagingMemSize/4];
    private int getAvailableStagingOffsetWord()
    {
        for (int i = 0; i < usedRegStagingMem.length; i++) {
            if(!usedRegStagingMem[i])
                return i;
        }

        System.err.println("寄存器暂存区容量不足");
        return 0;
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
                        refTimes.put(var.address,refTimes.getOrDefault(var.address,0)+1);
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
                if (register == null) {
                    System.out.println("寄存器分配失败");
                    register = stageOneRegAndUseIt(); //把一个寄存器的内容暂存到内存里，然后使用这个寄存器
                }
                regDesc.put(register,key);
                varDesc.put(key, new RegOrMem(register));
            } else {
                RegOrMem regOrMem = varDesc.get(key);
                if(!regOrMem.inMem) //不在内存里，直接使用这个寄存器
                    register = regOrMem.reg;
                else{
                    register = getStagedFromMem(key,regOrMem);
                }
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

    private Reg getStagedFromMem(AddressRWInfo key,RegOrMem regOrMem) {
        Reg tmp = getTmpRegister(); //如果寄存器依然不够，会有另一个寄存器被保存到内存里，给它他让位子

        //因为我们获取的是临时寄存器，所以会被标记为准备释放。从readyToReleaseReg中移除，不然结束后它将被释放
        readyToReleaseReg.remove(tmp);

        /*dataHolder.addAndLoadFromFuncData(builder, FunctionDataHolder.RegFuncData.getInstance(),
                                          tmp);*/


        int offset = regOrMem.memOffset;//getAvailableStagingOffset();

        builder.add(tmp,Regs.FP,AsmUtil.getRegStageOffsetFP()+offset * ConstDef.WORD_SIZE);
        builder.ldr(tmp,tmp,0);
        usedRegStagingMem[offset] = false;
        RegOrMem newRegOrMem = new RegOrMem(tmp);
        varDesc.put(key, newRegOrMem );
        builder.commit("load stage data of "+ key.address +" to "+tmp);
        return tmp;
    }

    private Reg stageOneRegAndUseIt() {
        Optional<Reg> minRefOpt =
                getUsingReg().stream().filter(r-> !usingRegThisIR.contains(r)).min(Comparator.comparingInt(r -> {
            AddressRWInfo addressRWInfo = regDesc.get(r);
            return refTimes.getOrDefault(addressRWInfo.address, 0);
        }));
        Reg reg;
        if(minRefOpt.isPresent()) //找到了引用数最小的
        {
            // 找到一个引用数最少的变量对应的寄存器，把他存进内存里
            reg = minRefOpt.get();
        }else{
            reg = usableRegs[0];
        }


        if(regDesc.containsKey(reg) && regDesc.get(reg)!=null)
        {
            /*dataHolder.addAndLoadFromFuncData(builder, FunctionDataHolder.RegFuncData.getInstance(),
                                              Regs.R12);*/
            int offsetWord = getAvailableStagingOffsetWord();
            builder.add(Regs.R12,Regs.FP,AsmUtil.getRegStageOffsetFP()+offsetWord * ConstDef.WORD_SIZE);
            builder.str(reg,Regs.R12,0);
            RegOrMem regOrMem = new RegOrMem(offsetWord);

            AddressRWInfo addressRWInfo = regDesc.get(reg);
            varDesc.put(addressRWInfo,regOrMem); //修改原来的映射信息

            usedRegStagingMem[offsetWord] = true;
            builder.commit("stage "+addressRWInfo.address+ " in " +reg);
        }

        return reg;
    }

    /*public Map<AddressRWInfo, Reg> getMapOfIR(InterRepresent ir) {
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
    }*/


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
        System.out.println("获取临时寄存器失败");
        Reg reg = stageOneRegAndUseIt();
        usingRegThisIR.add(reg);
        readyToReleaseReg.add(reg);
        return reg;
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

    public static class RegOrMem{
        boolean inMem = false;
        Reg reg;
        int memOffset;

        public RegOrMem(Reg reg) {
            this.reg = reg;
        }

        public RegOrMem(int memOffset) {
            inMem=true;
            this.memOffset = memOffset;
        }
    }
}
