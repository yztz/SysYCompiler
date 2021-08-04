package asm.allocator;

import asm.FunctionContext;
import asm.IName;
import asm.code.AsmFactory;
import asm.code.Code;
import ast.IAstValue;
import ast.Immediate;
import common.ILabel;
import common.OffsetVar;
import common.Register;
import common.symbol.Variable;
import ir.Temp;
import ir.code.IR;

import java.util.*;

public class RegisterAllocator {
    public Describer describer;
    private BasicBlock block;
    private List<Code> codes;
    private FunctionContext context;

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
            if (variable.isGlobal()) {  // 全局变量
                ILabel label = context.globalVarMap.get(variable);
                codes.add(AsmFactory.lea(register, label.getLabelName()));
                codes.add(AsmFactory.ldrWithoutOffset(register, register));
            } else {    // 局部变量
                codes.add(AsmFactory.ldrFromStack(register, context.getVariableOffset(variable)));
            }
        } else if (name instanceof OffsetVar) {
            OffsetVar offsetVar = ((OffsetVar) name);
            IAstValue offset = offsetVar.getOffset();
            Variable variable = offsetVar.variable;

            if (variable.isGlobal()) {  // 全局数组
                ILabel label = context.globalVarMap.get(variable);
                codes.add(AsmFactory.lea(register, label.getLabelName()));
                if (offset instanceof Immediate) {
                    codes.add(AsmFactory.ldrFromRegWithOffset(register, register, 4 * ((Immediate) offset).value));
                } else {
                    Register offsetReg = allocReg4rVal((IName) offset);
                    codes.add(AsmFactory.ldrFromRegWithOffset(register, register, offsetReg));
                }
            } else if (variable.isParam) {  // 形参数组
                if (offset instanceof Immediate) {
                    codes.add(AsmFactory.ldrFromStack(register, context.getVariableOffset(variable)));
                    codes.add(AsmFactory.ldrFromRegWithOffset(register, register, 4 * ((Immediate) offset).value));
                } else {
                    Register offsetReg = allocReg4rVal((IName) offset);
                    codes.add(AsmFactory.ldrFromStack(register, context.getVariableOffset(variable)));
                    codes.add(AsmFactory.ldrFromRegWithOffset(register, register, offsetReg));
                }
            } else {    // 局部数组
                int arrayAddress = context.getArrayAddress(variable);
                if (offset instanceof Immediate) {
                    codes.add(AsmFactory.ldrFromStack(register, arrayAddress + ((Immediate) offset).value * 4));
                } else {
                    codes.add(AsmFactory.add(register, Register.fp, arrayAddress));
                    Register offsetReg = allocReg4rVal((IName) offset);
                    codes.add(AsmFactory.ldrFromRegWithOffset(register, register, offsetReg));
                }
            }
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
        if (name instanceof Temp || describer.isInMemory(name)) {
            describer.freeName(name);
            return;
        }
        Register register = describer.getRegister(name);
        if (name instanceof Variable) {
            Variable variable = ((Variable) name);
            if (!variable.isConst) {
                if (variable.isGlobal()) {  // 全局变量
                    codes.add(AsmFactory.code(String.format("@ save %s:global", variable.name)));
                    ILabel label = context.globalVarMap.get(variable);
                    Register addr = allocFreeReg();
                    codes.add(AsmFactory.lea(addr, label.getLabelName()));
                    codes.add(AsmFactory.strWithoutOffset(register, addr));
                } else {    // 局部变量
                    codes.add(AsmFactory.code(String.format("@ save %s:local", variable.name)));
                    codes.add(AsmFactory.strStack(register, context.getVariableOffset(variable)));
                }
            }
        } else if (name instanceof OffsetVar) {
            OffsetVar offsetVar = ((OffsetVar) name);
            Variable variable = offsetVar.variable;
            IAstValue offset = offsetVar.getOffset();
            if (!variable.isConst) {
                if (variable.isGlobal()) {  // 全局数组
                    ILabel label = context.globalVarMap.get(variable);
                    Register addr = allocFreeReg();
                    codes.add(AsmFactory.lea(addr, label.getLabelName()));
                    if (offset instanceof IName) {
                        Register offsetReg = allocReg4rVal((IName) offset);
                        codes.add(AsmFactory.strWithRegOffset(register, addr, offsetReg));
                    } else {
                        codes.add(AsmFactory.strWithOffset(register, addr, 4 * ((Immediate) offset).value));
                    }
                } else if (variable.isParam) {
                    if (offset instanceof Immediate) {
                        Register addr = allocFreeReg();
                        // 加载地址
                        codes.add(AsmFactory.ldrFromStack(addr, context.getVariableOffset(variable)));
                        // 传送数据
                        codes.add(AsmFactory.strWithOffset(register, register, 4 * ((Immediate) offset).value));
                    } else {
                        Register offsetReg = allocReg4rVal((IName) offset);
                        Register addr = allocFreeReg();
                        // 加载地址
                        codes.add(AsmFactory.ldrFromStack(addr, context.getVariableOffset(variable)));
                        // 传送数据
                        codes.add(AsmFactory.strWithRegOffset(register, addr, offsetReg));
                    }
                } else {    // 局部数组
                    int arrayAddress = context.getArrayAddress(variable);
                    if (offset instanceof Immediate) {
                        codes.add(AsmFactory.strStack(register, arrayAddress + ((Immediate) offset).value * 4));
                    } else {
                        Register addr = allocFreeReg();
                        Register offsetReg = allocReg4rVal((IName) offset);
                        codes.add(AsmFactory.add(addr, Register.fp, arrayAddress));
                        codes.add(AsmFactory.strWithRegOffset(register, addr, offsetReg));
                    }
                }
            }
        } else {
            System.err.printf("%s 无法保存%n", name);
            return;
        }
        describer.freeName(name);
    }

    private Register spill() {
        Register reg = null;
        int minCost = Integer.MAX_VALUE;
        for (Register register : Describer.availableReg) {
            int cost = getCost(register, currentIR);
            if (cost < minCost) {
                minCost = cost;
                reg = register;
            }
        }
        saveReg(reg);
        return reg;
    }

    /*
        申请空闲寄存器， 保证该寄存器仅在本次ir中使用
     */
    public Register allocFreeReg() {
        Register register = describer.getFreeRegister();
        if (register == null) {
            register = spill();
        }
        return register;
    }

    private Register allocReg4rVal(IName name) {
        Register reg;
        reg = describer.getRegister(name);
        if (reg != null) return reg;
        reg = describer.getFreeRegister();
        if (reg != null) {
            loadName(reg, name);
            return reg;
        }
        reg = spill();
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
        List<IName> rVals = ir.getRVal();
        for (IName rVal : rVals) {
            if (ir.refMap.get(rVal).nextRef == null) {
                reg = allocReg4rVal(rVal);
                describer.updateName(reg, lVal);
                return reg;
            }
        }
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
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case ADD:
                rn = allocReg4rVal((IName) ir.op2);
                rd = allocReg4lVal(ir);
                if (ir.op3 instanceof IName) {
                    rm = allocReg4rVal(((IName) ir.op3));
                    ret.put((IName) ir.op3, rm);
                }
                ret.put((IName) ir.op1, rd);
                ret.put((IName) ir.op2, rn);
                describer.updateName(rd, (IName) ir.op1);
                break;
            // 一个操作数，一个左值
            case NEGATE:
            case MINUS:
                rn = allocReg4rVal((IName) ir.op2);
                rd = allocReg4lVal(ir);
                ret.put(((IName) ir.op1), rd);
                ret.put(((IName) ir.op2), rn);
                describer.updateName(rd, (IName) ir.op1);
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
                    describer.updateName(rn, ((IName) ir.op1));
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
            // goto语句，可能存在比较
            case GE:
            case GT:
            case LE:
            case LT:
            case EQ:
            case NOT_EQ:
                if (ir.op2 != null) {
                    rn = allocReg4rVal(((IName) ir.op2));
                    rm = allocReg4rVal(((IName) ir.op3));
                    ret.put(((IName) ir.op2), rn);
                    ret.put(((IName) ir.op3), rm);
                }
                break;
            case CALL:
                if (null != ir.op1) {
                    Register register = allocReg4lVal(ir);
                    ret.put(((IName) ir.op1), register);
                }
        }

        killName();
        return ret;
    }

    /*
        杀死本条语句中不再活跃的变量
     */
    private void killName() {
        for (IName name : currentIR.refMap.keySet()) {
            Reference ref = currentIR.refMap.get(name);
            if (!ref.isAlive) { // 不再活跃
                saveName(name);
            }
        }
    }

    private int getCost(Register register, IR ir) {
        // 初始化代价为0
        int cost = 0;
        IName lVal = ir.getLVal();
        List<IName> rVal = ir.getRVal();
        // 获取R中存储的所有名字
        Set<IName> names = describer.getNames(register);
        for (IName name : names) {  // 遍历
            Set<Register> address = describer.getRegisters(name);   // 获取名字所在的所有寄存器
            if (address.size() == 1) {    // 当前名字不在其他R中
                if (lVal != name || rVal.contains(lVal)) {  // ！“该名字是左值，且左值不存在于右值”
                    if (isReferredAfter(name, ir)) cost++;  //
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