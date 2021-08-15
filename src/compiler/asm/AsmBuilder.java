package compiler.asm;

import compiler.Location;
import compiler.asm.operand.*;
import compiler.genir.code.IfGotoRepresent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
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
    private boolean _hookIfNotImmXX = false;
    //private boolean _hookBLProtectReg = false;
    private FunctionDataHolder dataHolder;
    private RegGetter regGetter;

    /**
     * 如果传入的立即数不是imm8m和imm12，builder会自动进行一些处理，确保生成的程序正确
     */
    public void hookIfNotImmXX(FunctionDataHolder dataHolder, RegGetter regGetter)
    {
        this._hookIfNotImmXX = true;
        this.dataHolder = dataHolder;
        this.regGetter = regGetter;
    }

    public int totalLineNum()
    {
        return building.statements.size();
    }
    /*public void hookBLProtectReg(RegGetter regGetter)
    {
        _hookBLProtectReg = true;
        this.regGetter = regGetter;
    }*/

    public static String toImm(long imm) {
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

    public AsmBuilder addDirective(String directive, String arg1, String arg2, String arg3) {
        building.add(String.format("\t.%s\t%s, %s, %s", directive, arg1, arg2,arg3));
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

    public AsmBuilder comm(String label,long byteSize) {
        return addDirective("comm",label,String.valueOf(byteSize));
    }

    public AsmBuilder space(long byteSize) {
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

    //-------------------------调试用--------------------------------
    public AsmBuilder lfb(int index) {
        return label(String.format(".LFB%d",index));
    }

    public AsmBuilder lfe(int index) {
        return label(String.format(".LFE%d",index));
    }

    public AsmBuilder lbb(int index) {
        return label(String.format(".LBB%d",index));
    }

    public AsmBuilder lbe(int index) {
        return label(String.format(".LBE%d",index));
    }
    public AsmBuilder loc(int fileNo, Location loc) {
        return addDirective(String.format("loc %d %d %d",fileNo,loc.lineNum,loc.colNum));
    }
    public AsmBuilder loc(int fileNo,int lineNum,int colNum) {
        return addDirective(String.format("loc %d %d %d",fileNo,lineNum,colNum));
    }

    public AsmBuilder file(String fileName)
    {
        return addDirective(String.format("file\t\"%s\"",fileName));
    }

    public AsmBuilder file(int index , String fileName)
    {
        return addDirective(String.format("file\t%d \"%s\"",index,fileName));
    }
    public AsmBuilder size(String targetLabel, long size) {
        return addDirective("size", targetLabel, String.valueOf(size));
    }
    //========================汇编指令部分===================================



    public AsmBuilder delayGen(Consumer<AsmBuilder> builder)
    {
        if(_hookIfNotImmXX)
            building.add(new AsmCodeSupplier(builder,dataHolder,regGetter));
        else
            building.add(new AsmCodeSupplier(builder));
        return this;
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

    public AsmBuilder regRegImmOrReg(RegRegImmOrRegOP op, Reg rd, Reg rn, int imm8m)
    {
        if(_hookIfNotImmXX)
        {
            if(!AsmUtil.imm8m(imm8m))
            {
                Reg tmp;
                if(rd!=rn)
                {
                    tmp = rd;
                }else{
                    tmp = regGetter.getTmpRegister();
                }
                mov(tmp,imm8m);
                regRegImmOrReg(op,rd,rn,tmp);
                regGetter.releaseReg(tmp);
            }
        }
        return addInstruction(op.getText(), rd.getText(), rn.getText(), String.format("#%d",imm8m));
    }
    public AsmBuilder regRegImmOrReg(RegRegImmOrRegOP op, Reg rd, Reg rn, Reg rs)
    {
        return addInstruction(op.getText(), rd.getText(), rn.getText(), rs.getText());
    }


    /**
     * 检查操作数是否合法，比如是否符合imm8m，如果不合法，会自动进行一些处理，可能会插入一些语句
     * @return 修复后的操作数
     */
    private Operand fixOperand(Operand operand)
    {
        if(operand instanceof RegShiftImmOperand)
        {
            RegShiftImmOperand rsi = (RegShiftImmOperand) operand;
            if(!AsmUtil.imm8m(rsi.immData))
            {
                Reg tmp = regGetter.getTmpRegister();
                //ldrEq(tmp, rsi.immData);
                dataHolder.addAndLoadFromFuncData(this,rsi.immData,tmp);
                return new RegShiftRegOperand(rsi.op,rsi.regM,tmp);
            }
        }

        if(operand instanceof ImmOperand)
        {
            ImmOperand rsi = (ImmOperand) operand;
            if(!AsmUtil.imm8m(rsi.imm8m))
            {
                Reg tmp = regGetter.getTmpRegister();
                //ldrEq(tmp, rsi.imm8m);
                dataHolder.addAndLoadFromFuncData(this,(int)rsi.imm8m,tmp);
                return new RegOperand(tmp);
            }
        }

        return operand;
    }

    public AsmBuilder regOperand(RegOperandOP op, Reg rd, Operand operand) {
        if(_hookIfNotImmXX)
            operand = fixOperand(operand);
        return addInstruction(op.getText(), rd.getText(), operand.getText());
    }

/*    public AsmBuilder jumpLabel(String op,String label)
    {
        return addInstruction(op,label);
    }*/
    // --------------------跳转指令-------------------------------------

    public AsmBuilder regRegOperand(RegRegOperandOP op, Reg rd, Reg rn, Operand operand) {
        if(_hookIfNotImmXX)
            operand = fixOperand(operand);
        return addInstruction(op.getText(), rd.getText(), rn.getText(), operand.getText());
    }

    public AsmBuilder cmp(Reg rd, Reg rn) {
        return regOperand(RegOperandOP.CMP, rd, new RegOperand(rn));
    }

    public AsmBuilder cmp(Reg rd, int imm8m) {
        if(_hookIfNotImmXX)
        {
            if(!AsmUtil.imm8m(imm8m))
            {
                Reg tmp = regGetter.getTmpRegister();
                //ldrEq(tmp,imm8m);
                dataHolder.addAndLoadFromFuncData(this,(int) imm8m,tmp);
                return regOperand(RegOperandOP.CMP, rd, new RegOperand(tmp));
            }
        }

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

        if(_hookIfNotImmXX)
        {
            if(offset>4095 || offset< -4095)
            {
                Reg tmp;
                if(op==Mem.LDR)
                    tmp = rd;
                else
                    tmp = regGetter.getTmpRegister();
                //ldrEq(tmp, offset);
                dataHolder.addAndLoadFromFuncData(this,offset,tmp);
                return mem(op,size,rd,rn,tmp,false,ShiftOp.LSL,0,saveOffset,postOffset);
            }
        }

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
        if(_hookIfNotImmXX)
        {
            if(offset>4095 || offset< -4095)
            {
                Reg baseReg = rd;
                Reg offsetReg = regGetter.getTmpRegister(0);
                ldr(baseReg,label,0);
                //ldrEq(offsetReg,Math.abs(offset));
                dataHolder.addAndLoadFromFuncData(this,offset,offsetReg);
                return mem(Mem.LDR,null,rd,baseReg,offsetReg,false,ShiftOp.LSL,0,false,false);
            }
        }
        return addInstruction("ldr", rd.getText(),
                              offset == 0 ?
                                      label :
                                      offset > 0 ?
                                              String.format("%s+%d", label, offset) :
                                              String.format("%s%d", label, offset));
    }

    /**
     * ldr伪指令,如果可以直接用mov，就用mov，不行的话就把imm放进常量池里，通过ldr加载
     */
    public AsmBuilder ldrEq(Reg rd, long imm)
    {
        return addInstruction("ldr",rd.getText(),String.format("=%d",imm));
    }

    public AsmBuilder ldrEq(Reg rd,String label)
    {
        return addInstruction("ldr",rd.getText(),String.format("=%s",label));
    }

    public AsmBuilder ldr(Reg rd, Reg rn, int offset) {
        return mem(Mem.LDR, null, rd, rn, offset, false, false);
    }

    public AsmBuilder str(Reg rd, Reg rn, int offset) {
        if(_hookIfNotImmXX)
        {
            if(offset>4095 || offset< -4095)
            {
                Reg offsetReg = regGetter.getTmpRegister(0);
                //ldrEq(offsetReg,Math.abs(offset));
                dataHolder.addAndLoadFromFuncData(this,offset,offsetReg);
                return mem(Mem.STR,null,rd,rn,offsetReg,false,ShiftOp.LSL,0,false,false);
            }
        }
        return mem(Mem.STR, null, rd, rn, offset, false, false);
    }


    public AsmBuilder push(Reg[] regs, boolean lr) {

        return push(Arrays.asList(regs), lr);
    }

    public AsmBuilder push(List<Reg> regs, boolean lr) {
        String regList = regs.stream().map(Reg::getText).collect(Collectors.joining(","));
        if (lr) {
            return addInstruction("push", String.format("{%s,lr}", regList));
        }
        return addInstruction("push", String.format("{%s}", regList));
    }

    public AsmBuilder pop(Reg[] regs, boolean pc) {
        return pop(Arrays.asList(regs), pc);
    }
    public AsmBuilder pop(List<Reg> regs, boolean pc) {
        String regList = regs.stream().map(Reg::getText).collect(Collectors.joining(","));
        if (pc) {
            return addInstruction("pop", String.format("{%s,pc}", regList));
        }
        return addInstruction("pop", String.format("{%s}", regList));
    }

    public AsmBuilder ldm(LSAddressMode mode, Reg rd, List<Reg> regsTarget) {
        return lsm("ldm", mode, rd, regsTarget);
    }

    public AsmBuilder stm(LSAddressMode mode, Reg rd, List<Reg> regsTarget) {
        return lsm("stm", mode, rd, regsTarget);
    }

    //----------------------------------常用-----------------------------------

    public AsmBuilder mov(Reg rd, Operand operand) {
        return regOperand(RegOperandOP.MOV, rd, operand);
    }

    public AsmBuilder mov(Reg rd, Reg rn) {
        return regOperand(RegOperandOP.MOV, rd, new RegOperand(rn));
    }

    public AsmBuilder mov(Reg rd, int imm8m) {
        RegOperandOP op;
        int actNum;
        if(imm8m>=0){
            op = RegOperandOP.MOV;
            actNum=imm8m;
        }
        else{
            op = RegOperandOP.MVN;
            actNum = -imm8m-1;
        }

        if(_hookIfNotImmXX)
        {
            if(!AsmUtil.imm8m(actNum))
            {
                dataHolder.addAndLoadFromFuncData(this,imm8m,rd);
                return this;
            }
        }
        return addInstruction(op.getText(), rd.getText(), String.format("#%d", actNum));
    }

    public AsmBuilder mov(Cond cond,Reg rd,int imm8m)
    {
        if(_hookIfNotImmXX)
        {
            if(!AsmUtil.imm8m(imm8m))
            {
                //ldrEq(rd, imm8m);
                dataHolder.addAndLoadFromFuncData(this,imm8m,rd);
                return this;
            }
        }
        return addInstruction(
                String.format("%s%s",RegOperandOP.MOV.getText(),cond.getText()), rd.getText(), String.format("#%d",
                                                                                                          imm8m));
    }

    public AsmBuilder add(Reg rd, Reg rn, Operand operand) {
        return regRegOperand(RegRegOperandOP.ADD, rd, rn, operand);
    }

    public AsmBuilder add(Reg rd, Reg rn, Reg rm) {
        return regRegOperand(RegRegOperandOP.ADD, rd, rn, new RegOperand(rm));
    }

    public AsmBuilder add(Reg rd, Reg rn, int imm8m) {
        if(_hookIfNotImmXX)
        {
            if(!AsmUtil.imm8m(imm8m))
            {
                Reg tmp;
                if(rd!=rn)
                    tmp = rd; //节约寄存器
                else
                    tmp = regGetter.getTmpRegister();
                //ldrEq(tmp, imm8m);
                dataHolder.addAndLoadFromFuncData(this,imm8m,tmp);
                return add(rd,rn,tmp);
            }
        }
        return addInstruction(RegRegOperandOP.ADD.getText(), rd.getText(), rn.getText(), toImm(imm8m));
    }

    public AsmBuilder sub(Reg rd, Reg rn, Operand operand) {

        return regRegOperand(RegRegOperandOP.SUB, rd, rn, operand);
    }

    public AsmBuilder sub(Reg rd, Reg rn, Reg rm) {
        return regRegOperand(RegRegOperandOP.SUB, rd, rn, new RegOperand(rm));
    }

    public AsmBuilder sub(Reg rd, Reg rn, int imm8m) {
        if(_hookIfNotImmXX)
        {
            if(!AsmUtil.imm8m(imm8m))
            {
                Reg tmp;
                if(rd!=rn)
                    tmp = rd; //节约寄存器
                else
                    tmp = regGetter.getTmpRegister();
                //ldrEq(tmp, imm8m);
                dataHolder.addAndLoadFromFuncData(this,imm8m,tmp);
                return sub(rd,rn,tmp);
            }
        }
        return addInstruction(RegRegOperandOP.SUB.getText(), rd.getText(), rn.getText(), toImm(imm8m));
    }

    public AsmBuilder mul(Reg rd,Reg rn,Reg rm)
    {
        return tripleReg(TripleRegOP.MUL,rd,rn,rm);
    }

    public AsmBuilder sdiv(Reg rd,Reg rn,Reg rm)
    {
        return tripleReg(TripleRegOP.SDIV,rd,rn,rm);
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

    public AsmSection getSectionAndStartNew() {
        AsmSection ended = building;
        building = new AsmSection();
        return ended;
    }




    /**
     * @return
     */
    private AsmBuilder lsm(String op, LSAddressMode mode, Reg rd, List<Reg> regsTarget) {
        String regList = regsTarget.stream().map(Reg::getText).collect(Collectors.joining(","));
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

    public enum TripleRegOP{
        ADD, SUB, MUL, //乘
        MLA, //乘并加
        MLS, //乘并减
        UMLL, UMLAL, UMAAL, SDIV,//有符号除
        UDIV,//无符号除
        ASR,//算数右移
        LSL,//逻辑左移
        LSR,//逻辑右移
        ROR;//循环右移

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum RegRegImmOrRegOP {
        ASR,//算数右移
        LSL,//逻辑左移
        LSR,//逻辑右移
        ROR;//循环右移

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

    public enum Cond{
        EQ,//(等于，Equal) ==
        NE,//(不等于，Not Equal) !=
        CS,//(高于或同于,进位设置，Carry Set) >=
        CC,//(低于,进位清除，Carry Clear) <
        MI,//(负号，MInus) < 0
        PL,//(正号，PLus) >=0
        VS,//(溢出设置，oVerflow Set)
        VC,//(溢出清除，oVerflow Clear)
        HI,//(高于，HIgher) >
        LS,//(低于或同于，Lower or Same) <=
        GE,//(大于等于，Greater or equal) >=
        LT,//(小于，Less Than) <
        GT,//(大于，Greater Than) >
        LE,//(小于等于，Less or equal) <=
        AL,//(总是，Always) 永真
        NV,
        ;//(从不，Never ) 永假

        public static Cond getFromRelOP(IfGotoRepresent.RelOp op)
        {
            switch (op)
            {
                case LESS:
                    return LT;
                case GREATER:
                    return GT;
                case LESS_EQUAL:
                    return LE;
                case GREATER_EQUAL:
                    return GE;
                case EQUAL:
                    return EQ;
                case NOT_EQUAL:
                    return NE;
            }

            return NE;
        }

        public String getText() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
