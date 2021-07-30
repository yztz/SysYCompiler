package compiler.asm;

import compiler.Util;
import compiler.asm.operand.ImmOperand;
import compiler.asm.operand.Operand;
import compiler.asm.operand.RegOperand;
import compiler.asm.operand.ShiftOp;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class AsmBuilder {

    private final String label;
    public boolean lrModified = false;
    private AsmSection building;
    public AsmBuilder() {
        building = new AsmSection();
        label = null;
    }

    public AsmBuilder(String label) {
        this.label = label;
        building = new AsmSection();
    }

    public static String toImm(int imm) {
        return String.format("#%d", imm);
    }

    //==========伪指令部分====================================

    /*
    lr寄存器是否会被修改
     */
    public boolean isLrModified() {
        return lrModified;
    }

    public AsmBuilder addDirective(String directive) {
        building.add(String.format("\t.%s", directive));
        return this;
    }

    public AsmBuilder addDirective(String directive, String arg) {
        building.add(String.format("\t.%s\t%s", directive, arg));
        return this;
    }

    public AsmBuilder addDirective(String directive, String arg1, String arg2) {
        building.add(String.format("\t.%s\t%s, %s", directive, arg1, arg2));
        return this;
    }

    public AsmBuilder sectionType(String type)
    {
        return addDirective("section",type);
    }
    public AsmBuilder word(int value) {
        return addDirective("word", String.valueOf(value));
    }

    public AsmBuilder word(String label) {
        return addDirective("word", label);
    }

    public AsmBuilder label(String label) {
        building.add(String.format("%s:", label));
        return this;
    }

    public AsmBuilder label() {
        return label(label);
    }

    public AsmBuilder data() {
        return addDirective("data");
    }

    public AsmBuilder bss() {
        return addDirective("bss");
    }

    public AsmBuilder comm(String label,int byteSize) {
        return addDirective("comm",label,String.valueOf(byteSize));
    }

    public AsmBuilder space(int byteSize) {
        return addDirective("space",String.valueOf(byteSize));
    }

    public AsmBuilder text() {
        return addDirective("text");
    }

    public AsmBuilder arch(String arg) {
        return addDirective("arch", arg);
    }

    public AsmBuilder arm() {
        return addDirective("arm");
    }

    public AsmBuilder fpu(String arg) {
        return addDirective("fpu", arg);
    }

    public AsmBuilder global(String ident) {
        //globl global都行
        return addDirective("globl", ident);
    }

    public AsmBuilder global() {
        return global(label);
    }

    public AsmBuilder align(int size) {
        return addDirective("align", String.valueOf(size));
    }

    public AsmBuilder align(int size, byte fillData) {
        return addDirective("align", String.valueOf(size), String.valueOf(fillData));
    }

    public AsmBuilder type(Type type) {
        return type(label, type);
    }

    public AsmBuilder type(String targetLabel, Type type) {
        switch (type) {
            case Object:
                return addDirective("type", targetLabel, "%object");
            case Function:
                return addDirective("type", targetLabel, "%function");
        }

        return this;
    }

    public AsmBuilder size(int size) {
        return size(label, size);
    }


    //========================汇编指令部分===================================

    public AsmBuilder size(String targetLabel, int size) {
        return addDirective("size", targetLabel, String.valueOf(size));
    }
    public AsmBuilder addInstruction(String op, String r1, String r2, String r3, String r4) {
        building.add(String.format("\t%s\t%s, %s, %s, %s", op, r1, r2, r3,r4));
        return this;
    }
    public AsmBuilder addInstruction(String op, String r1, String r2, String r3) {
        building.add(String.format("\t%s\t%s, %s, %s", op, r1, r2, r3));
        return this;
    }

    public AsmBuilder addInstruction(String op, String r1, String r2) {
        building.add(String.format("\t%s\t%s, %s", op, r1, r2));
        return this;
    }

    public AsmBuilder addInstruction(String op, String r1) {
        building.add(String.format("\t%s\t%s", op, r1));
        return this;
    }

    public AsmBuilder quadrupleReg(QuadOP op,Reg rdLo,Reg rdHi,Reg rm,Reg rn)
    {
        return addInstruction(op.getText(),rdLo.getText(),rdHi.getText(),rm.getText(),rn.getText());
    }

    public AsmBuilder tripleReg(TripleRegOP op, Reg rd, Reg rn, Reg rm) {
        return addInstruction(op.getText(), rd.getText(), rn.getText(), rm.getText());
    }

    public AsmBuilder regOperand(RegOperandOP op, Reg rd, Operand operand) {
        return addInstruction(op.getText(), rd.getText(), operand.getText());
    }

/*    public AsmBuilder jumpLabel(String op,String label)
    {
        return addInstruction(op,label);
    }*/
    // --------------------跳转指令-------------------------------------

    public AsmBuilder regRegOperand(RegRegOperandOP op, Reg rd, Reg rn, Operand operand) {
        return addInstruction(op.getText(), rd.getText(), rn.getText(), operand.getText());
    }

    public AsmBuilder cmp(Reg rd, Reg rn) {
        return regOperand(RegOperandOP.CMP, rd, new RegOperand(rn));
    }

    public AsmBuilder cmp(Reg rd, int imm8m) {
        return regOperand(RegOperandOP.CMP, rd, new ImmOperand(imm8m));
    }

    public AsmBuilder bxx(CondB cond, String label) {
        return addInstruction(cond.getText(), label);
    }

    public AsmBuilder b(String label) {
        return addInstruction("b", label);
    }

    public AsmBuilder bl(String label) {
        lrModified = true;
        return addInstruction("bl", label);
    }

    public AsmBuilder bx(Reg reg) {
        return addInstruction("bx", reg.getText());
    }

    // -------------------- 内存读写-----------------------------------
    public AsmBuilder mem(Mem op, Size size, Reg rd, Reg rn, int offset, boolean saveOffset, boolean postOffset) {
        String s = size == null ? "" : size.getText();

        if (postOffset) //操作完成后，变址
        {
            return addInstruction(op.getText() + s, rd.getText(), String.format("[%s], #%d", rn.getText(), offset));
        } else { //操作前变址
            return addInstruction(op.getText() + s, rd.getText(), saveOffset ? //保存变址结果到地址寄存器
                    String.format("[%s, #%d]!", rn.getText(), offset) : String.format("[%s, #%d]", rn.getText(),
                                                                                      offset));
        }
    }

    public AsmBuilder mem(Mem op, Size size, Reg rd, Reg rn, Reg rm, boolean rmNeg, ShiftOp shiftOp, int shift, boolean saveOffset, boolean postOffset) {
        String s = size == null ? "" : size.name();

        if (postOffset) //操作完成后，变址
        {
            return addInstruction(op.getText() + s, rd.getText(),
                                  String.format("[%s], %s%s, %s#%d", rn.getText(), rmNeg ? "-" : "", rm.getText(),
                                                shiftOp.getText(), shift));
        } else if (shift == 0) { //操作前变址


            return addInstruction(op.getText() + s, rd.getText(),
                                  String.format("[%s, %s%s]%s", rn.getText(), rmNeg ? "-" : "", rm.getText(),
                                                saveOffset ? "!" : ""));
        } else {

            // op{size} rd, [rn,+/-rm, shift_op#shift]{!}
            return addInstruction(op.getText() + s, rd.getText(),
                                  String.format("[%s, %s%s, %s#%d]%s", rn.getText(), rmNeg ? "-" : "", rm.getText(),
                                                shiftOp.getText(), shift, saveOffset ? "!" : ""));
        }
    }

    public AsmBuilder ldr(Reg rd, String label, int offset) {
        return addInstruction("ldr", rd.getText(),
                              offset == 0 ?
                                      label :
                                      offset > 0 ?
                                              String.format("%s+%d", label, offset) :
                                              String.format("%s%d", label, offset));
    }

    public AsmBuilder sdr(Reg rd,Reg rn,int offset)
    {
        return mem(Mem.STR,null,rd,rn,offset,false,false);
    }

    public AsmBuilder ldr(Reg rd, Reg rn, int offset) {
        return mem(Mem.LDR, null, rd, rn, offset, false, false);
    }

    public AsmBuilder str(Reg rd, Reg rn, int offset) {
        return mem(Mem.STR, null, rd, rn, offset, false, false);
    }

    public AsmBuilder push(Reg[] regs, boolean lr) {
        String regList = Arrays.stream(regs).map(Reg::getText).collect(Collectors.joining(","));
        if (lr) {
            return addInstruction("push", String.format("{%s,lr}", regList));
        }
        return addInstruction("push", String.format("{%s}", regList));
    }

    public AsmBuilder pop(Reg[] regs, boolean pc) {
        String regList = Arrays.stream(regs).map(Reg::getText).collect(Collectors.joining(","));
        if (pc) {
            return addInstruction("pop", String.format("{%s,pc}", regList));
        }
        return addInstruction("pop", String.format("{%s}", regList));
    }

    public AsmBuilder ldm(LSAddressMode mode, Reg rd, Reg[] regsTarget) {
        return lsm("ldm", mode, rd, regsTarget);
    }

    public AsmBuilder stm(LSAddressMode mode, Reg rd, Reg[] regsTarget) {
        return lsm("stm", mode, rd, regsTarget);
    }

    //----------------------------------常用-----------------------------------

    public AsmBuilder mov(Reg rd, Operand operand) {
        return regOperand(RegOperandOP.MOV, rd, operand);
    }

    public AsmBuilder mov(Reg rd, Reg rn) {
        return regOperand(RegOperandOP.MOV, rd, new RegOperand(rn));
    }

    public AsmBuilder mov(Reg rd, int imm12) {
        return addInstruction(RegOperandOP.MOV.getText(), rd.getText(), String.format("#%d", imm12));
    }

    public AsmBuilder add(Reg rd, Reg rn, Operand operand) {
        return regRegOperand(RegRegOperandOP.ADD, rd, rn, operand);
    }

    public AsmBuilder add(Reg rd, Reg rn, Reg rm) {
        return regRegOperand(RegRegOperandOP.ADD, rd, rn, new RegOperand(rm));
    }

    public AsmBuilder add(Reg rd, Reg rn, int imm12) {
        return addInstruction(RegRegOperandOP.ADD.getText(), rd.getText(), rn.getText(), toImm(imm12));
    }

    public AsmBuilder sub(Reg rd, Reg rn, Operand operand) {
        return regRegOperand(RegRegOperandOP.SUB, rd, rn, operand);
    }

    public AsmBuilder sub(Reg rd, Reg rn, Reg rm) {
        return regRegOperand(RegRegOperandOP.SUB, rd, rn, new RegOperand(rm));
    }

    public AsmBuilder sub(Reg rd, Reg rn, int imm12) {
        return addInstruction(RegRegOperandOP.SUB.getText(), rd.getText(), rn.getText(), toImm(imm12));
    }

    public AsmBuilder commit(String commit)
    {
        building.add(String.format("@%s",commit));
        return this;
    }

    public AsmSection getSection() {
        return building;
    }

    public void setSection(AsmSection section) {
        building = section;
    }

    public AsmSection startNew() {
        AsmSection ended = building;
        building = new AsmSection();
        return ended;
    }




    /**
     * @return
     */
    private AsmBuilder lsm(String op, LSAddressMode mode, Reg rd, Reg[] regsTarget) {
        String regList = Arrays.stream(regsTarget).map(Reg::getText).collect(Collectors.joining(","));
        return addInstruction(String.format("%s%s", op, mode.getText()), rd.getText(), String.format("{%s}", regList));
    }

    public enum Type {
        Object, Function
    }

    public enum RegOperandOP {
        MOV, MVN, CMP, CMN, TST, TEQ;

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum RegRegOperandOP {
        ADD, ADC, // 带进位
        SUB, SBC, // 带进位
        RSB, // 反向减
        RSC, AND, // 与
        EOR, // 异或
        ORR, // 或
        ORN, // 或非
        BIC; // rd = rn AND NOT Operand

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum TripleRegOP {
        ADD, SUB, MUL, //乘
        MLA, //乘并加
        MLS, //乘并减
        UMLL, UMLAL, UMAAL, SDIV,//有符号除
        UDIV;//无符号除

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum QuadOP{
        SMULL;

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }


    public enum Mem {
        LDR, STR;

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Size {
        B, //字节
        SB, //有符号字节
        H, //半字
        SH, // 有符号半字
        W; //字

        public String getText() {
            if (this == W) {
                return "";
            } else {
                return name().toLowerCase(Locale.ROOT);
            }
        }
    }

    public enum LSAddressMode {
        IA, //increase after
        IB, //increase before
        DA, //decrease after
        DB, //decrease before
        ED, //empty decrease
        FD, //full decrease
        EA, //empty
        FA, NONE; //full

        public String getText() {
            if (this == NONE) return "";
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum CondB {
        BEQ,//相等
        BNE,//不等
        BPL,//非负
        BMI,//负
        BCC,//无进位
        BCS,//有进位
        BLO,//小于（无符号数）
        BHS,//大于等于（无符号数）
        BHI,//大于（无符号数）
        BLS,//小于等于（无符号数）
        BVC,//无溢出（有符号数）
        BVS,//有溢出（有符号数）
        BGT,//大于（有符号数）
        BGE,//大于等于（有符号数）
        BLT,//小于（有符号数）
        BLE //小于等于（有符号数）
        ;

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
