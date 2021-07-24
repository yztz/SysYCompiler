package asm;

import asm.operand.ImmOperand;
import asm.operand.Operand;
import asm.operand.RegOperand;
import asm.operand.ShiftOp;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public class AsmBuilder {
    private final AsmSection building;
    private final String label;

    public AsmBuilder() {
        building = new AsmSection();
        label = null;
    }

    public AsmBuilder(String label) {
        this.label = label;
        building = new AsmSection();
    }

    //==========伪指令部分====================================

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

    public AsmBuilder word(int value) {
        return addDirective("word", String.valueOf(value));
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

    public AsmBuilder size(String targetLabel, int size) {
        return addDirective("size", targetLabel, String.valueOf(size));
    }


    //========================汇编指令部分===================================

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

    public AsmBuilder tripleReg(TripleRegOP op, Reg rd, Reg rn, Reg rm) {
        return addInstruction(op.name(), rd.getText(), rn.getText(), rm.getText());
    }

    public AsmBuilder regOperand(RegOperandOP op, Reg rd, Operand operand) {
        return addInstruction(op.name(), rd.getText(), operand.getText());
    }

    public AsmBuilder regRegOperand(RegRegOperandOP op, Reg rd, Reg rn, Operand operand) {
        return addInstruction(op.name(), rd.getText(), rn.getText(), operand.getText());
    }

/*    public AsmBuilder jumpLabel(String op,String label)
    {
        return addInstruction(op,label);
    }*/
    // --------------------跳转指令-------------------------------------

    public AsmBuilder b(String label) {
        return addInstruction("bl", label);
    }

    public AsmBuilder bl(String label) {
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
            return addInstruction(op + s, rd.getText(),
                                  String.format("[%s, %s%s, %s#%d]%s", rn.getText(), rmNeg ? "-" : "", rm.getText(),
                                                shiftOp.getText(), shift, saveOffset ? "!" : ""));
        }
    }

    public AsmBuilder ldr(String label)
    {
        return addInstruction("ldr",label);
    }

    //----------------------------------常用-----------------------------------

    public AsmBuilder mov(Reg rd,Operand operand)
    {
        return regOperand(RegOperandOP.MOV,rd,operand);
    }
    public AsmBuilder mov(Reg rd,Reg rn)
    {
        return regOperand(RegOperandOP.MOV,rd,new RegOperand(rn));
    }
    public AsmBuilder mov(Reg rd,int imm12)
    {
        return addInstruction(RegOperandOP.MOV.getText(),rd.getText(),String.format("#%d",imm12));
    }

    public AsmBuilder add(Reg rd,Reg rn,Operand operand)
    {
        return regRegOperand(RegRegOperandOP.ADD,rd,rn,operand);
    }

    public AsmBuilder add(Reg rd,Reg rn,Reg rm)
    {
        return regRegOperand(RegRegOperandOP.ADD,rd,rn,new RegOperand(rm));
    }

    public AsmBuilder add(Reg rd,Reg rn,int imm12)
    {
        return addInstruction(RegRegOperandOP.ADD.getText(),rd.getText(),rn.getText(),toImm(imm12));
    }

    public AsmBuilder sub(Reg rd,Reg rn,Operand operand)
    {
        return regRegOperand(RegRegOperandOP.SUB,rd,rn,operand);
    }

    public AsmBuilder sub(Reg rd,Reg rn,Reg rm)
    {
        return regRegOperand(RegRegOperandOP.SUB,rd,rn,new RegOperand(rm));
    }

    public AsmBuilder sub(Reg rd,Reg rn,int imm12)
    {
        return addInstruction(RegRegOperandOP.SUB.getText(),rd.getText(),rn.getText(),toImm(imm12));
    }

    public AsmBuilder push(Reg[] regs,boolean lr)
    {
        String regList = Arrays.stream(regs).map(Reg::getText).collect(Collectors.joining(","));
        if(lr)
        {
            return addInstruction("push",String.format("{%s,lr}",regList));
        }
        return addInstruction("push",String.format("{%s}",regList));
    }
    public AsmBuilder pop(Reg[] regs,boolean pc)
    {
        String regList = Arrays.stream(regs).map(Reg::getText).collect(Collectors.joining(","));
        if(pc)
        {
            return addInstruction("push",String.format("{%s,pc}",regList));
        }
        return addInstruction("push",String.format("{%s}",regList));
    }

    public static String toImm(int imm)
    {
        return String.format("#%d",imm);
    }

    public AsmSection getBuild() {
        return building;
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
        ADD,
        ADC, // 带进位
        SUB,
        SBC, // 带进位
        RSB, // 反向减
        RSC,
        AND, // 与
        EOR, // 异或
        ORR, // 或
        ORN, // 或非
        BIC; // rd = rn AND NOT Operand

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum TripleRegOP {
        ADD,
        SUB,
        MUL, //乘
        MLA, //乘并加
        MLS, //乘并减
        UMLL,
        UMLAL,
        UMAAL,
        SDIV,//有符号除
        UDIV,//无符号除
    }

    public enum Mem {
        LDR, STR;
        public String getText()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Size {
        B, //字节
        SB, //有符号字节
        H, //半字
        SH, // 有符号半字
        W; //字
        public String getText()
        {
            if(this==W)
            {
                return "";
            }else{
                return name().toLowerCase(Locale.ROOT);
            }
        }
    }

    public enum LSAddressMode{
        IA, //increase after
        IB, //increase before
        DA, //decrease after
        DB, //decrease before
        ED, //todo
        FD,
        EA,
        FA,
    }
}
