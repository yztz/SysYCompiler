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
    public int baseOffset = 0;  // (r - 1) * 4
    public int frameSize;
    public int localSize;

    public boolean isLrUsed = false;
    public ILabel returnLabel;
    private int paramCount = 0;
    private List<Variable> initVars = new ArrayList<>();


    public FunctionContext(Function function) {
        this.function = function;
        this.irs = function.irs;
        this.function.blocks = BasicBlock.genBlocks(irs);
        for (BasicBlock block : function.blocks) block.printBlock();

        analyze();
        genHead();
        loadParam();
        initVariable();
        genBody();
        genTail();
    }

    private void enableLR() {
        this.isLrUsed = true;
        baseOffset = 4;
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
            /* 分析全局变量的使用情况 */
//            ir.getNames().forEach(name -> {
//                if (name instanceof Variable) {
//                    Variable var = (Variable) name;
//                    if (var.isGlobal()) {   // 引用了全局变量
//                        globalVarMap.put(var, Label.newLabel(String.format("%s.L%d", function.name, nextLabelID++)));
//                    }
//                } else if (name instanceof OffsetVar) {
//                    Variable var = ((OffsetVar) name).variable;
//                    if (var.isGlobal()) {   // 引用了全局变量
//                        globalVarMap.put(var, Label.newLabel(String.format("%s.L%d", function.name, nextLabelID++)));
//                    }
//                }
//            });
        }

        /* 分析数组定义 */
        function.getVariables().forEach(variable -> {
            if (variable.isArray && variable.isInit) initVars.add(variable);
        });
        if (!initVars.isEmpty()) enableLR();

        /* 分析栈帧大小 */
        frameSize = Utils.align8(function.totalOffset + maxParamBytes + baseOffset + 4);
        localSize = frameSize - 4 - baseOffset;
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
            codes.add(AsmFactory.add(Register.r0, Register.fp, getVariableOffset(variable)));
            codes.add(AsmFactory.bl("memset"));
        }
    }


    public void loadParam() {
        List<Variable> params = function.getParams();
        for (int i = 0; i < params.size() && i < Function.PARAM_LIMIT; i++) {
            Variable param = params.get(i);
            codes.add(AsmFactory.strStack(Register.valueOf("r" + i), getVariableOffset(param)));
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
        if (isLrUsed) {
            codes.add(AsmFactory.code("push {fp, lr}"));
            codes.add(AsmFactory.add(Register.fp, Register.sp, 4));
        } else {
            codes.add(AsmFactory.code("push {fp}"));
            codes.add(AsmFactory.add(Register.fp, Register.sp, 0));
        }
        codes.add(AsmFactory.sub(Register.sp, Register.sp, localSize));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void genBody() {
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
                regMap = allocator.getReg(ir);
                if (ir.isJump() || ir.isReturn()) allocator.saveAll();

                switch (ir.op) {
                    case ADD:
                        rd = regMap.get(ir.op1);
                        rn = regMap.get(ir.op2);
                        if (ir.op3 instanceof Immediate) {
                            codes.add(AsmFactory.add(rd, rn, ((Immediate) ir.op3).value));
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.add(rd, rn, rm));
                        }
                        break;
                    case SUB:
                        rd = regMap.get(ir.op1);
                        if (ir.op2 instanceof Immediate) {
                            rn = regMap.get(ir.op3);
                            codes.add(AsmFactory.rsb(rd, rn, ((Immediate) ir.op2).value));
                        } else {
                            rn = regMap.get(ir.op2);
                            if (ir.op3 instanceof Immediate) {
                                codes.add(AsmFactory.sub(rd, rn, ((Immediate) ir.op3).value));
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
                                codes.add(AsmFactory.mov(rn, ((Immediate) ir.op2).value));
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
                        paramCount = 0;
                        Function targetFunc = ((Function) ir.op2);
                        codes.add(AsmFactory.bl(targetFunc.getLabelName()));
                        if (targetFunc.hasReturn()) {
                            rd = regMap.get(ir.op1);
                            codes.add(AsmFactory.mov(rd, Register.r0));
                        }

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
                            codes.add(AsmFactory.cmp(rn, ((Immediate) ir.op3).value));
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
                            codes.add(AsmFactory.cmp(rn, ((Immediate) ir.op3).value));
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
                            codes.add(AsmFactory.cmp(rn, ((Immediate) ir.op3).value));
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
                            codes.add(AsmFactory.cmp(rn, ((Immediate) ir.op3).value));
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
                            codes.add(AsmFactory.cmp(rn, ((Immediate) ir.op3).value));
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
                            codes.add(AsmFactory.cmp(rn, ((Immediate) ir.op3).value));
                        } else {
                            rm = regMap.get(ir.op3);
                            codes.add(AsmFactory.cmp(rn, rm));
                        }
                        codes.add(AsmFactory.movWhen(rd, 1, "ne"));
                        codes.add(AsmFactory.movWhen(rd, 0, "eq"));
                        codes.add(AsmFactory.uxtb(rd));
                        break;
                }
                allocator.killName();
            }
            if (!lastIR.isJump()) allocator.saveAll();

        }
    }


    public void genTail() {
        /* 添加返回标签 */
        codes.add(AsmFactory.label(returnLabel.getLabelName()));
        /* 恢复 */
        if (isLrUsed) {
            codes.add(AsmFactory.sub(Register.sp, Register.fp, 4));
            codes.add(AsmFactory.code("pop {fp, pc}"));
        } else {
            codes.add(AsmFactory.add(Register.sp, Register.fp, 0));
            codes.add(AsmFactory.code("ldr fp, [sp], #4"));
            codes.add(AsmFactory.bx(Register.lr));
        }
        /* 生成全局变量的地址引用 */
//        codes.add(AsmFactory.align(2));
//        for (Map.Entry<Variable, ILabel> entry : globalVarMap.entrySet()) {
//            Variable var = entry.getKey();
//            ILabel label = entry.getValue();
//            codes.add(AsmFactory.label(label.getLabelName()));
//            codes.add(AsmFactory.word(var.name));
//        }
        /* 长度 */
        codes.add(AsmFactory.size(function.name, ".-" + function.name));
    }


    public int getVariableOffset(Variable variable) {
        if (variable.paramIndex < 4) {   // 栈内地址
            return -variable.offset - baseOffset - 4;
        } else {
            return -variable.offset;
        }
    }

}
