package asm;

import asm.allocator.BasicBlock;
import asm.allocator.RegisterAllocator;
import asm.code.AsmFactory;
import asm.code.Code;
import ast.Immediate;
import common.*;
import common.symbol.Function;
import common.symbol.Variable;
import ir.code.IR;

import java.util.*;

public class FunctionContext {
    private static int nextLabelID = 0;

    public Function function;
    public List<Code> codes = new LinkedList<>();
    public List<IR> irs;
    private Set<Register> savedRegs = new HashSet<>();
    //    public int baseOffset = 4;  // (r - 1) * 4
    public int frameSize;
    public int localSize;

    public ILabel returnLabel;
    private int paramCount = 0;
    private List<Variable> initVars = new ArrayList<>();

    private int pushHook;
    private int popHook;


    public FunctionContext(Function function) {
        this.function = function;
        this.irs = function.irs;
        this.function.blocks = BasicBlock.genBlocks(irs);
        for (BasicBlock block : function.blocks) block.printBlock();
        this.savedRegs.add(Register.r4);
        this.savedRegs.add(Register.fp);

        analyze();
        genHead();
        loadParam();
        initVariable();
        genBody();
        genTail();
        genHook();
    }

    private void genHook() {
        List<Register> regs = new ArrayList<>(savedRegs);
        // 排序
        regs.sort(Register.comparator);
        // push hook
        codes.set(pushHook, AsmFactory.push(regs));
        // pop hook
        int lrIdx = regs.indexOf(Register.lr);
        if (lrIdx != -1) regs.set(lrIdx, Register.pc);
        codes.set(popHook, AsmFactory.pop(regs));
    }

    private void enableLR() {
        this.savedRegs.add(Register.lr);
    }

    public void analyze() {
        // 分析lr寄存器是否使用(1. 用户函数调用情况 2. 初始化memest or memcopy) -> 函数偏移的基址
        // 分析变量的使用(1. 生成局部变量初始化值 2. 生成)

        /* 分析调用函数行为 */
        /* 分析调用函数所需的栈空间大小 */
        int maxParamBytes = 0;
        for (IR ir : irs) {
            if (ir.op == OP.CALL) { // 分析函数调用情况以及调用函数所需的最大参数空间
                enableLR();
                Function callee = (Function) ir.op2;
                maxParamBytes = Math.max(maxParamBytes, callee.getParamBytes() - 16);
            }
        }
        /* 分析寄存器使用 */
        for (BasicBlock block : function.blocks) {
            RegisterAllocator allocator = new RegisterAllocator(this, block);
            IR lastIR = null;
            for (IR ir : block.getIRs()) {
                lastIR = ir;
                allocator.getReg(ir);
                // 如果当前语句是跳转语句，则在该语句之前生成保存语句
                if (ir.isJump() || ir.isReturn()) allocator.saveAll();
                // 在每天语句末尾删除不相关的名字
                allocator.killName();
            }
            // 基本块的最后一条不是跳转语句，主动生成保存语句
            if (!lastIR.isJump() && !lastIR.isReturn()) allocator.saveAll();
            savedRegs.addAll(allocator.usedRegs);
        }
        codes.clear();

        /* 分析数组定义 */
        function.getVariables().forEach(variable -> {
            if (variable.isArray && variable.isInit) initVars.add(variable);
        });
        if (!initVars.isEmpty()) enableLR();

        /* 分析栈帧大小 */
        frameSize = Utils.align8(function.totalOffset + maxParamBytes + 4 * savedRegs.size());
        localSize = frameSize - 4 * savedRegs.size();
        codes.add(AsmFactory.code(String.format("@ function\t[%s]", function.name)));
        codes.add(AsmFactory.code(String.format("@ frameSize\t[%d]", frameSize)));
        codes.add(AsmFactory.code(String.format("@ localSize\t[%d]", localSize)));
//        /* 记录局部数组起始栈帧位置 */
//        int loc = maxParamBytes - frameSize;
//        for (Variable variable : function.getVariables()) {
//            if (variable.isArray) {
//                arrayAddress.put(variable, loc);
//                loc += variable.getBytes();
//            }
//        }
        /* 生成返回标签 */
        returnLabel = Label.newLabel(function.name + ".return");
    }

    public void initVariable() {
        for (Variable variable : initVars) {
//            System.out.println(variable.offset);
            codes.add(AsmFactory.note(String.format("init %s, size: %d", variable, variable.getBytes())));
            codes.add(AsmFactory.mov(Register.r2, variable.getBytes()));
            codes.add(AsmFactory.mov(Register.r1, 0));
            int offset = getVariableOffset(variable);
            if (Utils.imm8m(offset)) {
                codes.add(AsmFactory.add(Register.r0, Register.fp, offset));
            } else {
                codes.add(AsmFactory.mov(Register.r0, offset));
                codes.add(AsmFactory.add(Register.r0, Register.fp, Register.r0));
            }
            codes.add(AsmFactory.bl("memset"));
        }
    }


    public void loadParam() {
        List<Variable> params = function.getParams();
        for (int i = 0; i < params.size() && i < Function.PARAM_LIMIT; i++) {
            Variable param = params.get(i);
            codes.add(AsmFactory.strWithOffset(Register.valueOf("r" + i), Register.fp, getVariableOffset(param)));
        }
    }

    public void genHead() {
        codes.add(AsmFactory.align(2));
        codes.add(AsmFactory.global(function.name));
//        codes.add(AsmFactory.arch("armv7ve"));
        codes.add(AsmFactory.syntax("unified"));
        codes.add(AsmFactory.arm());
        codes.add(AsmFactory.fpu("vfp"));
        codes.add(AsmFactory.type(function.name, "function"));

        codes.add(AsmFactory.label(function.name));
        pushHook = codes.size();
        codes.add(AsmFactory.code(null));
        codes.add(AsmFactory.add(Register.fp, Register.sp, 4 * (savedRegs.size() - 1)));
        if (Utils.imm8m(localSize)){
            codes.add(AsmFactory.sub(Register.sp, Register.sp, localSize));
        } else {
            codes.add(AsmFactory.mov(Register.r4, localSize));
            codes.add(AsmFactory.sub(Register.sp, Register.sp, Register.r4));
        }
    }


    @SuppressWarnings("SuspiciousMethodCalls")
    public void genBody() {
        // todo 可以提取重复的合法立即数判断代码
        for (BasicBlock block : function.blocks) {
            Map<IName, Register> regMap;
            RegisterAllocator allocator = new RegisterAllocator(this, block);
            IR lastIR = null;
            for (IR ir : block.getIRs()) {
                if (null != ir.label) codes.add(AsmFactory.label(ir.label.getLabelName()));
                codes.add(AsmFactory.code(String.format("@ %s", ir)));
                lastIR = ir;
                Register rd, rn, rm;
                ILabel target;
                int imm;
                regMap = allocator.getReg(ir);
                // 如果当前语句是跳转语句，则在该语句之前生成保存语句
                if (ir.isJump() || ir.isReturn()) allocator.saveAll();

                switch (ir.op) {
                    case ADD:
                        rd = regMap.get(ir.op1);
                        rn = regMap.get(ir.op2);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)) {
                                codes.add(AsmFactory.add(rd, rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.add(rd, rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.add(rd, rn, rm));
                        }
                        break;
                    case SUB:
                        rd = regMap.get(ir.op1);
                        if (ir.op2 instanceof Immediate) {
                            imm = ((Immediate) ir.op2).value;
                            rn = regMap.get(ir.op3);
                            if (Utils.imm8m(imm)) {
                                codes.add(AsmFactory.rsb(rd, rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.rsb(rd, rn, rd));
                            }
                        } else {
                            rn = regMap.get(ir.op2);
                            if (ir.op3 instanceof Immediate) {
                                imm = ((Immediate) ir.op3).value;
                                if (Utils.imm8m(imm)) {
                                    codes.add(AsmFactory.sub(rd, rn, imm));
                                } else {
                                    codes.add(AsmFactory.mov(rd, imm));
                                    codes.add(AsmFactory.sub(rd, rn, rd));
                                }
                            } else {
                                rm = regMap.get(ir.op3);
                                codes.add(AsmFactory.sub(rd, rn, rm));
                            }
                        }
                        break;
                    case MUL:
                        rd = regMap.get(ir.op1);
                        rn = regMap.get(ir.op2);
                        if (ir.op3 instanceof Immediate) {
                            Register tmp = allocator.allocFreeReg();
                            codes.add(AsmFactory.mov(tmp, ((Immediate) ir.op3).value));
                            codes.add(AsmFactory.mul(rd, rn, tmp));
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.mul(rd, rn, rm));
                        }
                        break;
                    case DIV:
                        rd = regMap.get(ir.op1);
                        //rn = regMap.get(ir.op2);
                        if (ir.op2 instanceof Immediate) {
                            rm = regMap.get(ir.op3);
                            Register tmp = allocator.allocFreeReg();
                            codes.add(AsmFactory.mov(tmp, ((Immediate) ir.op2).value));
                            codes.add(AsmFactory.div(rd, tmp, rm));
                        } else {
                            rn = regMap.get(ir.op2);
                            if (ir.op3 instanceof Immediate) {
                                Register tmp = allocator.allocFreeReg();
                                codes.add(AsmFactory.mov(tmp, ((Immediate) ir.op3).value));
                                codes.add(AsmFactory.div(rd, rn, tmp));
                            } else {
                                rm = regMap.get(ir.op3);
                                codes.add(AsmFactory.div(rd, rn, rm));
                            }
                        }
                        break;
                    case MOD:
                        rd = regMap.get(ir.op1);
                        if (ir.op2 instanceof Immediate) {
                            rn = allocator.allocFreeReg();
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.mov(rn, ((Immediate) ir.op2).value));
                            codes.add(AsmFactory.div(rd, rn, rm));
                            codes.add(AsmFactory.mls(rd, rm, rd, rn));
                        } else {
                            rn = regMap.get(ir.op2);
                            if (ir.op3 instanceof Immediate) {
                                rm = allocator.allocFreeReg();
                                codes.add(AsmFactory.mov(rm, ((Immediate) ir.op3).value));
                                codes.add(AsmFactory.div(rd, rn, rm));
                                codes.add(AsmFactory.mls(rd, rm, rd, rn));
                            } else {
                                rm = regMap.get(ir.op3);
                                codes.add(AsmFactory.div(rd, rn, rm));
                                codes.add(AsmFactory.mls(rd, rm, rd, rn));
                            }
                        }
                        break;
                    case MINUS:
                        rd = regMap.get(ir.op1);
                        rn = regMap.get(ir.op2);
                        codes.add(AsmFactory.rsb(rd, rn, 0));
                        break;
                    case NEGATE:
                        rd = regMap.get(ir.op1);
                        rn = regMap.get(ir.op2);
                        codes.add(AsmFactory.cmp(rn, 0));
                        codes.add(AsmFactory.movWhen(rd, 1, "eq"));
                        codes.add(AsmFactory.movWhen(rd, 0, "ne"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                    case ASSIGN:
                        //...
                        break;
                    case CALL:
                        // 调用
                        paramCount = 0;
                        Function targetFunc = ((Function) ir.op2);
                        codes.add(AsmFactory.bl(targetFunc.getLabelName()));
                        // 获取返回值
                        if (targetFunc.hasReturn()) {
                            rd = regMap.get(ir.op1);
                            codes.add(AsmFactory.mov(rd, Register.r0));
                        }
//                        allocator.restoreState();
                        break;
                    case PARAM:
                        //todo
                        if (paramCount > Function.PARAM_LIMIT - 1) {
                            int offset = 4 * (paramCount - Function.PARAM_LIMIT);
                            if (ir.op1 instanceof Immediate) {  // 参数为立即数
                                rd = allocator.allocFreeReg();
                                codes.add(AsmFactory.mov(rd, ((Immediate) ir.op1).value));
                                codes.add(AsmFactory.strWithOffset(rd, Register.sp, offset));
                            } else {
                                rd = regMap.get(ir.op1);
                                codes.add(AsmFactory.strWithOffset(rd, Register.sp, offset));
                            }
                        } else {
//                            if (paramCount == 0) allocator.saveState();
                            if (ir.op1 instanceof Immediate) {
                                codes.add(AsmFactory.mov(Register.valueOf("r" + paramCount), ((Immediate) ir.op1).value));
                            } else {
                                rd = regMap.get(ir.op1);
                                codes.add(AsmFactory.mov(Register.valueOf("r" + paramCount), rd));
                            }
                        }
                        paramCount++;
                        break;
                    case GOTO:
                        codes.add(AsmFactory.b(((ILabel) ir.op1).getLabelName()));
                        break;
                    case RETURN:
                        if (function.hasReturn()) {
                            if (ir.op1 instanceof Immediate) {
                                codes.add(AsmFactory.mov(Register.r0, ((Immediate) ir.op1).value));
                            } else if (null == ir.op1) {
                                codes.add(AsmFactory.mov(Register.r0, 0));
                            } else {
                                rd = regMap.get(ir.op1);
                                codes.add(AsmFactory.mov(Register.r0, rd));
                            }
                        }
                        codes.add(AsmFactory.b(returnLabel.getLabelName()));
                        break;
                    case COND_GOTO:
                        target = ((ILabel) ir.op1);
                        rn = regMap.get(ir.op2);
                        codes.add(AsmFactory.cmp(rn, 0));
                        codes.add(AsmFactory.bWhen(target.getLabelName(), "ne"));
                        break;
                    case GE:
                        rn = regMap.get(ir.op2);
                        rd = regMap.get(ir.op1);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)){
                                codes.add(AsmFactory.cmp(rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.cmp(rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "ge"));
                        codes.add(AsmFactory.movWhen(rd, 0, "lt"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                    case GT:
                        rn = regMap.get(ir.op2);
                        rd = regMap.get(ir.op1);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)){
                                codes.add(AsmFactory.cmp(rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.cmp(rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "gt"));
                        codes.add(AsmFactory.movWhen(rd, 0, "le"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                    case LE:
                        rn = regMap.get(ir.op2);
                        rd = regMap.get(ir.op1);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)){
                                codes.add(AsmFactory.cmp(rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.cmp(rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "le"));
                        codes.add(AsmFactory.movWhen(rd, 0, "gt"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                    case LT:
                        rn = regMap.get(ir.op2);
                        rd = regMap.get(ir.op1);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)){
                                codes.add(AsmFactory.cmp(rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.cmp(rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "lt"));
                        codes.add(AsmFactory.movWhen(rd, 0, "ge"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                    case EQ:
                        rn = regMap.get(ir.op2);
                        rd = regMap.get(ir.op1);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)){
                                codes.add(AsmFactory.cmp(rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.cmp(rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "eq"));
                        codes.add(AsmFactory.movWhen(rd, 0, "ne"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                    case NOT_EQ:
                        rn = regMap.get(ir.op2);
                        rd = regMap.get(ir.op1);
                        if (ir.op3 instanceof Immediate) {
                            imm = ((Immediate) ir.op3).value;
                            if (Utils.imm8m(imm)){
                                codes.add(AsmFactory.cmp(rn, imm));
                            } else {
                                codes.add(AsmFactory.mov(rd, imm));
                                codes.add(AsmFactory.cmp(rn, rd));
                            }
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "ne"));
                        codes.add(AsmFactory.movWhen(rd, 0, "eq"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                }
                // 在每天语句末尾删除不相关的名字
                allocator.killName();
            }
            // 基本块的最后一条不是跳转语句，主动生成保存语句
            if (!lastIR.isJump() && !lastIR.isReturn()) allocator.saveAll();
//            savedRegs.addAll(allocator.usedRegs);
        }
    }


    public void genTail() {
        /* 添加返回标签 */
        codes.add(AsmFactory.label(returnLabel.getLabelName()));
        /* 恢复 */
        codes.add(AsmFactory.sub(Register.sp, Register.fp, 4 * (savedRegs.size() - 1)));
//        if (savedRegs.contains(Register.lr)) {
//            savedRegs.set(savedRegs.indexOf(Register.lr), Register.pc);
//            codes.add(AsmFactory.pop(savedRegs));
//        } else {
//            codes.add(AsmFactory.pop(popHook));
//            codes.add(AsmFactory.bx(Register.lr));
//        }
        popHook = codes.size();
        if (savedRegs.contains(Register.lr)) {
            codes.add(AsmFactory.code(null));
        } else {
            codes.add(AsmFactory.code(null));
            codes.add(AsmFactory.bx(Register.lr));
        }

        /* 长度 */
        codes.add(AsmFactory.size(function.name, ".-" + function.name));
    }


    public int getVariableOffset(Variable variable) {
        if (variable.paramIndex < 4) {   // 栈内地址
            return -variable.offset - 4 * savedRegs.size();
        } else {
            return -variable.offset;
        }
    }

}
