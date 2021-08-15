package asm.allocator;

import asm.FunctionContext;
import asm.IName;
import asm.code.AsmFactory;
import asm.code.Code;
import ast.IAstValue;
import ast.Immediate;
import common.OffsetVar;
import common.Register;
import common.Utils;
import common.symbol.Function;
import common.symbol.Variable;
import common.Temp;
import ir.code.IR;

import java.util.*;

public class RegisterAllocator {
    public Set<Register> usedRegs = new HashSet<>();
    public Describer describer;
    private BasicBlock block;
    private List<Code> codes;
    private FunctionContext context;

    private Set<Register> releaseSet = new HashSet<>();

    private IR currentIR;


    public RegisterAllocator(FunctionContext context, BasicBlock block) {
        this.context = context;
        this.block = block;
        this.describer = new Describer(block);
        this.codes = context.codes;
        //        genGraph();
    }


    private void loadName(Register register, IName name) {
        describer.loadName(register, name);

        if (name instanceof Variable) {
            Variable variable = ((Variable) name);
            if (variable.isArray) { // 数组名
                if (variable.isGlobal()) {
//                    ILabel label = context.globalVarMap.get(variable);
                    codes.add(AsmFactory.lea(register, variable.name));
                } else if (variable.isParam) {
                    codes.add(AsmFactory.ldrWithOffset(register, Register.fp, context.getVariableOffset(variable)));
                } else {
                    int offset = context.getVariableOffset(variable);
                    Register offsetReg = loadImm(offset);
                    if (null == offsetReg) {
                        codes.add(AsmFactory.add(register, Register.fp, offset));
                    } else {
                        codes.add(AsmFactory.add(register, Register.fp, offsetReg));
                    }
                }
            } else {
                if (variable.isGlobal()) {  // 全局变量
//                    ILabel label = context.globalVarMap.get(variable);
                    codes.add(AsmFactory.lea(register, variable.name));
                    codes.add(AsmFactory.ldrWithoutOffset(register, register));
                } else {    // 局部变量
                    int offset = context.getVariableOffset(variable);
                    Register offsetReg = loadOffset(offset);
                    if (null == offsetReg) {
                        codes.add(AsmFactory.ldrWithOffset(register, Register.fp, offset));
                    } else {
                        codes.add(AsmFactory.ldrWithOffset(register, Register.fp, offsetReg, false));
                    }
                }
            }
        }
        else if (name instanceof OffsetVar) {
            OffsetVar offsetVar = ((OffsetVar) name);
            IAstValue offset = offsetVar.getOffset();
            Variable variable = offsetVar.variable;

            Register addr = allocReg4rVal(variable);
            if (offsetVar.isAddress) {  // 带偏移量的地址
                if (offset instanceof Immediate) {
                    int imm = 4 * ((Immediate) offset).value;
                    Register offsetReg = loadImm(imm);
                    if (null == offsetReg) {
                        codes.add(AsmFactory.add(register, addr, imm));
                    } else {
                        codes.add(AsmFactory.add(register, addr, offsetReg));
                    }
                } else {
                    Register offsetReg = allocReg4rVal((IName) offset);
                    codes.add(AsmFactory.add(register, addr, offsetReg, true));
                }
            } else {    //
                if (offset instanceof Immediate) {
                    int imm = 4 * ((Immediate) offset).value;
                    Register offsetReg = loadOffset(imm);
                    if (null == offsetReg) {
                        codes.add(AsmFactory.ldrWithOffset(register, addr, imm));
                    } else {
                        codes.add(AsmFactory.mov(register, imm));
                        codes.add(AsmFactory.ldrWithOffset(register, addr, offsetReg, false));
                    }
                } else {
                    Register offsetReg = allocReg4rVal((IName) offset);
                    codes.add(AsmFactory.ldrWithOffset(register, addr, offsetReg, true));
                }
            }
        }
        else if (name instanceof Temp) {
            Temp tmp = ((Temp) name);
            if (tmp.paramIdx > Function.PARAM_LIMIT - 1) {
                int offset = (tmp.paramIdx - Function.PARAM_LIMIT) * 4;
                codes.add(AsmFactory.ldrWithOffset(register, Register.sp, offset));
            }
        }
    }

    public Register loadImm(int imm) {
        if (Utils.imm8m(imm)) {
            return null;
        } else {
            codes.add(AsmFactory.mov(Register.ip, imm));
            return Register.ip;
        }
    }

    public Register loadOffset(int offset) {
        if (Utils.isLegalOffset(offset)) {
            return null;
        } else {
            codes.add(AsmFactory.mov(Register.ip, offset));
            return Register.ip;
        }
    }

    public void saveAll() {
        for (Register register : Describer.availableReg) {
            saveReg(register);
        }
    }


    /*
        保存register中的所有变量至内存
     */
    private void saveReg(Register register) {
        Set<IName> names = describer.getNames(register);
        for (IName name : names) {
            saveName(name);
        }
        describer.freeReg(register);
    }

    private void saveName(IName name) {
//        System.out.printf("[saveName] save name: %s%n", name);
        if (describer.isInMemory(name)) {
            describer.freeName(name);
            return;
        }
        Register register = describer.getRegister(name);
        if (name instanceof Variable) {
            Variable variable = ((Variable) name);
            if (!variable.isConst) {
                if (variable.isGlobal()) {  // 全局变量
                    codes.add(AsmFactory.code(String.format("@ save %s:global", variable.name)));
                    Register addr = Register.ip;
                    codes.add(AsmFactory.lea(addr, variable.name));
                    codes.add(AsmFactory.strWithoutOffset(register, addr));
                } else {    // 局部变量
                    codes.add(AsmFactory.code(String.format("@ save %s:local", variable.name)));
                    int offset = context.getVariableOffset(variable);
                    Register offsetReg = loadOffset(offset);
                    if (null == offsetReg) {
                        codes.add(AsmFactory.strWithOffset(register, Register.fp, offset));
                    } else {
                        codes.add(AsmFactory.strWithOffset(register, Register.fp, offsetReg, false));
                    }
                }
            }
        }
        else if (name instanceof OffsetVar) {
            OffsetVar offsetVar = ((OffsetVar) name);
            Variable variable = offsetVar.variable;
            IAstValue offset = offsetVar.getOffset();

            if (!variable.isConst) {
                if (variable.isParam || variable.isGlobal()) {
                    Register addr = allocReg4rVal(variable);
                    if (offset instanceof Immediate) {
                        int imm = 4 * ((Immediate) offset).value;
                        // 传送数据
                        Register offsetReg = loadOffset(imm);
                        if (null == offsetReg) {
                            codes.add(AsmFactory.strWithOffset(register, addr, imm));
                        } else {
                            codes.add(AsmFactory.strWithOffset(register, addr, offsetReg, false));
                        }
                    } else {
                        Register offsetReg = allocReg4rVal((IName) offset);
                        // 传送数据
                        codes.add(AsmFactory.strWithOffset(register, addr, offsetReg, true));
                    }
                } else {    // 局部数组
                    int arrayAddress = context.getVariableOffset(variable);
                    if (offset instanceof Immediate) {
                        int imm = arrayAddress + ((Immediate) offset).value * 4;
                        Register offsetReg = loadOffset(imm);
                        if (null == offsetReg) {
                            codes.add(AsmFactory.strWithOffset(register, Register.fp, imm));
                        } else {
                            codes.add(AsmFactory.strWithOffset(register, Register.fp, offsetReg, false));
                        }
                    } else {
                        Register addr = allocReg4rVal(variable);
                        Register offsetReg = allocReg4rVal((IName) offset);
                        codes.add(AsmFactory.strWithOffset(register, addr, offsetReg, true));
                    }
                }
            }
        }
        else if (name instanceof Temp) {
            Temp tmp = ((Temp) name);
            if (tmp.paramIdx > Function.PARAM_LIMIT - 1) {
                int offset = (tmp.paramIdx - Function.PARAM_LIMIT) * 4;
                codes.add(AsmFactory.strWithOffset(register, Register.sp, offset));
            } else if (tmp.paramIdx >= 0) {
                codes.add(AsmFactory.mov(Register.valueOf("r" + tmp.paramIdx), register));
            }
        }
        describer.freeName(name);
    }

//    private void saveState() {
//        IName savedName = null;
//        for (Register register : Describer.availableReg) {
//            Set<IName> names = describer.getNames(register);
//            for (IName name : names) {
//                // 当前名字是tmp
//                if (name instanceof Temp && isReferredAfter(name, currentIR)) {
//                    codes.add(AsmFactory.mov(Register.r4, register));
//                    savedName = name;
//                }
//            }
//        }
//        saveAll();
//        if (savedName != null)
//            describer.updateName(Register.r4, savedName);
//    }


    private Register spill() {
        Register reg = null;

        int minCost = Integer.MAX_VALUE;
        for (Register register : Describer.availableReg) {
            if (releaseSet.contains(register)) continue;
            if (describer.isLocked(register)) continue;

            int cost = getCost(register, currentIR);
            if (cost < minCost) {
                minCost = cost;
                reg = register;
            }
        }
//        System.out.printf("[spill] select reg: %s%n", reg);
        releaseSet.add(reg);
        saveReg(reg);
        releaseSet.remove(reg);
        return reg;
    }


    /*
        申请空闲寄存器， 保证该寄存器仅在本次ir中使用
     */
    private Register allocFreeReg() {
        Register register = describer.getFreeRegister();
        if (register == null) {
//            System.out.println("[allocFreeReg] spilled");
            register = spill();
            describer.lock(register);
        }
        usedRegs.add(register);
        return register;
    }

    private Register allocReg4rVal(IName name) {
        Register reg;
        reg = describer.getRegister(name);
        if (reg != null) return reg;
        reg = allocFreeReg();
        loadName(reg, name);
        return reg;
    }

    private Register allocReg4lVal(IR ir) {
        IName lVal = ir.getLVal();
        Register reg = describer.getRegister(lVal);
        if (reg != null && describer.getNames(reg).size() == 1) {
            describer.updateName(reg, lVal);
            return reg;
        }

        reg = allocFreeReg();
        if (reg != null) {
            describer.updateName(reg, lVal);
            return reg;
        }
        Set<IName> rVals = ir.getRVal();
        for (IName rVal : rVals) {
            if (ir.refMap.get(rVal).nextRef == null) {
                reg = allocReg4rVal(rVal);
                describer.updateName(reg, lVal);
                return reg;
            }
        }
//        System.out.printf("[allocReg4lVal] spilled for name: %s%n", lVal);
        reg = spill();
        describer.updateName(reg, lVal);
        return reg;
    }

    public Map<IName, Register> getReg(IR ir) {
        describer.unlock();
        this.currentIR = ir;
        Map<IName, Register> ret = new HashMap<>();
        Register rd, rn, rm;
        switch (ir.op) {
            // 有两个操作数，一个左值
            case GE:
            case GT:
            case LE:
            case LT:
            case EQ:
            case NOT_EQ:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case ADD:
                rd = allocReg4lVal(ir);
                if (ir.op2 instanceof IName) {
                    rn = allocReg4rVal((IName) ir.op2);
                    ret.put((IName) ir.op2, rn);
                }
                if (ir.op3 instanceof IName) {
                    rm = allocReg4rVal(((IName) ir.op3));
                    ret.put((IName) ir.op3, rm);
                }
                ret.put((IName) ir.op1, rd);

                break;
            // 一个操作数，一个左值
            case NEGATE:
            case MINUS:
                rn = allocReg4rVal((IName) ir.op2);
                rd = allocReg4lVal(ir);
                ret.put(((IName) ir.op1), rd);
                ret.put(((IName) ir.op2), rn);

//                codes.add(AsmFactory.cmp(rn, 0));
//                codes.add(AsmFactory.movWhen(rd, 1, "eq"));
//                codes.add(AsmFactory.movWhen(rd, 0, "ne"));
//                codes.add(AsmFactory.uxtb(rd));
                break;
            // 赋值语句
            case ASSIGN:
                if (ir.op2 instanceof IName && ir.op1 != ir.op2) {
                    rn = allocReg4rVal(((IName) ir.op2));
                    describer.copyName(rn, ((IName) ir.op1));
                } else {
                    rn = allocReg4lVal(ir);
                    codes.add(AsmFactory.mov(rn, ((Immediate) ir.op2).value));

                    //saveName(((IName) ir.op1));
                }
                break;
            // 仅有一个操作数
            case PARAM:
            case RETURN:
                if (ir.op1 instanceof IName) {
                    ret.put(((IName) ir.op1), allocReg4rVal(((IName) ir.op1)));
                }
                break;
            // cond goto
            case GE_GOTO:
            case GT_GOTO:
            case LE_GOTO:
            case LT_GOTO:
            case EQ_GOTO:
            case NOT_EQ_GOTO:
                ret.put(((IName) ir.op2), allocReg4rVal(((IName) ir.op2)));
                if (ir.op3 instanceof IName) ret.put(((IName) ir.op3), allocReg4rVal(((IName) ir.op3)));
                break;
            case CALL:
//                saveState();
                if (null != ir.op1) {
                    Register register = allocReg4lVal(ir);
                    ret.put(((IName) ir.op1), register);
                }
        }
        return ret;
    }

    /*
        杀死本条语句中不再活跃的变量
     */
    public void killName() {
        IName lVal = currentIR.getLVal();
        if (lVal != null) {
            Reference ref = currentIR.refMap.get(lVal);
            if (!ref.isAlive || ref.nextRef == null) { // 不再活跃
//                System.out.printf("try to kill [%s]%n", name);
                saveName(lVal);
            }
        }
        for (IName name : currentIR.getRVal()) {
            Reference ref = currentIR.refMap.get(name);
            if (!ref.isAlive || ref.nextRef == null) { // 不再活跃
//                System.out.printf("try to kill [%s]%n", name);
                saveName(name);
            }
        }
    }

    private int getCost(Register register, IR ir) {
        // 初始化代价为0
        int cost = 0;
        IName lVal = ir.getLVal();
        Set<IName> rVal = ir.getRVal();
        // 获取R中存储的所有名字
        Set<IName> names = describer.getNames(register);
        for (IName name : names) {  // 遍历
            if (describer.isInMemory(name)) continue;
            Set<Register> address = describer.getRegisters(name);   // 获取名字所在的所有寄存器
            if (address.size() == 1) {    // 当前名字不在其他R中
                if (lVal != name || rVal.contains(lVal)) {  // ！“该名字是左值，且左值不存在于右值”
                    if (isReferredAfter(name, ir)) {
                        cost++;  //
                        if (name instanceof Temp) {
                            Temp tmp = ((Temp) name);
                            cost++;
                            if (tmp.paramIdx < Function.PARAM_LIMIT) cost++;
                        }
                    }
                }
            }
        }

        return cost;
    }


    private boolean isReferredAfter(IName name, IR ir) {
        // 当前ir
        int idx = block.indexIR(ir);
        // 向后方遍历
        for (int i = idx; i < block.getIRs().size(); i++) {
            ir = block.getIR(i);
            if (ir.getRVal().contains(name)) {
                return true;
            }
        }
        return false;
    }
}
