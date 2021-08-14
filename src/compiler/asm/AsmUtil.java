package compiler.asm;

import compiler.ConstDef;
import compiler.genir.code.*;
import compiler.symboltable.*;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.initvalue.ArrayInitValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AsmUtil {

    /**
     * 遍历IR中的每个临时变量
     * @param ir
     * @param consumer
     */
    public static void traverseAddress(InterRepresent ir, Consumer<AddressRWInfo> consumer) {
        if (null == ir) return;

        if (ir instanceof BinocularRepre) {
            BinocularRepre tmp = (BinocularRepre) ir;
            if (!tmp.target.isData) consumer.accept(new AddressRWInfo(tmp.target, true));
            if (!tmp.sourceFirst.isData) consumer.accept(new AddressRWInfo(tmp.sourceFirst));
            if (!tmp.sourceSecond.isData) consumer.accept(new AddressRWInfo(tmp.sourceSecond));
        } else if (ir instanceof CallRepresent){
            CallRepresent tmp = (CallRepresent) ir;
            if (null != tmp.returnResult) consumer.accept(new AddressRWInfo(tmp.returnResult, true));
            if (null != tmp.params) {
                for (AddressOrData param : tmp.params) {
                    if (!param.isData) consumer.accept(new AddressRWInfo(param));
                }
            }
        } else if (ir instanceof IfGotoRepresent) {
            IfGotoRepresent tmp = (IfGotoRepresent) ir;
            if (!tmp.left.isData) consumer.accept(new AddressRWInfo(tmp.left));
            if (!tmp.right.isData) consumer.accept(new AddressRWInfo(tmp.right));
        } else if (ir instanceof LoadRepresent) {
            LoadRepresent tmp = (LoadRepresent) ir;
            consumer.accept(new AddressRWInfo(tmp.target, true));
        } else if (ir instanceof ReturnRepresent) {
            ReturnRepresent tmp = (ReturnRepresent) ir;
            if (null != tmp.returnData && !tmp.returnData.isData) consumer.accept(new AddressRWInfo(tmp.returnData));
        } else if (ir instanceof SaveRepresent) {
            SaveRepresent tmp = (SaveRepresent) ir;
            if (!tmp.target.isData) consumer.accept(new AddressRWInfo(tmp.target));
        } else if (ir instanceof UnaryRepre) {
            UnaryRepre tmp = (UnaryRepre) ir;
            consumer.accept(new AddressRWInfo(tmp.target, true));
            if (!tmp.source.isData) consumer.accept(new AddressRWInfo(tmp.source));
        }
    }

    public static String getVarLabel(FuncSymbol funcSymbol, SymbolDomain domain, ValueSymbol varSymbol)
    {
        return String.format(".L%s.%d.%s", funcSymbol == null ? "" : funcSymbol.getAsmLabel(),
                             domain.getId(), varSymbol.symbolToken.getText());
    }

    public static boolean isNeedInitInDataSection(HasInitSymbol varSymbol)
    {
        return (varSymbol instanceof VarSymbol && ((VarSymbol) varSymbol).hasConstInitValue) && varSymbol.initValues instanceof ArrayInitValue;
    }

    public static boolean isNeedInitInFuncData(HasInitSymbol varSymbol)
    {
        return (varSymbol instanceof VarSymbol && ((VarSymbol) varSymbol).hasConstInitValue) &&
                !AsmUtil.imm8m(varSymbol.initValues.get(0)) && !AsmUtil.negImm8m(varSymbol.initValues.get(0));
    }


    public static int getSymbolArrayIndexOffset(int arrayIndex)
    {
        return arrayIndex*ConstDef.WORD_SIZE;
    }

    public static int getSymbolOffset(ValueSymbol symbol)
    {
        return -symbol.getOffsetByte() - symbol.getByteSize();
    }

    public static int getSymbolOffset(ValueSymbol symbol, int arrayIndex)
    {
        return getSymbolOffset(symbol) + getSymbolArrayIndexOffset(arrayIndex);
    }

    //这些区域用来保存寄存器数据
    public static final int REG_DATA_LEN = 32;

    public static final int REG_STAGE_LEN = 128;//32个值

    public static int getFrameSize(int localSize,int maxParamCount)
    {
        int frameSize = ((int) Math.ceil(((double)localSize)/4.0 + maxParamCount))*4 + 8;
        return frameSize+REG_STAGE_LEN+REG_DATA_LEN;
    }

    public static int getSymbolOffsetFp(ValueSymbol symbol)
    {
        return getSymbolOffset(symbol)-2* ConstDef.WORD_SIZE- REG_DATA_LEN - REG_STAGE_LEN;
    }
    public static int getSymbolOffsetFp(ValueSymbol symbol, int arrayIndex)
    {
        return getSymbolOffset(symbol,arrayIndex) -2* ConstDef.WORD_SIZE - REG_DATA_LEN - REG_STAGE_LEN;
    }

    //需要保存fp,lr,所以往低地址偏移两个字
    public static int getRegOffsetFP()
    {
        return -REG_DATA_LEN -2* ConstDef.WORD_SIZE;
    }

    public static int getRegStageOffsetFP()
    {
        return getRegOffsetFP() - REG_STAGE_LEN;
    }

    public static int getParamOffsetCalledFp(int index)
    {
        return (index-4)*ConstDef.WORD_SIZE+4; //fp = sp-4
    }
    public static int getParamOffsetCallerSp(int index)
    {
        return (index-4)*ConstDef.WORD_SIZE;
    }


    /**
     * ror32 - rotate a 32-bit value right
     */
    public static int ror32(int word, int shift)
    {
        return (word >> (shift & 31)) | (word << ((-shift) & 31));
    }

    public static int rol32(int word, int shift)
    {
        return (word << shift) | (word >> ((-shift) & 31));
    }

    /*
     * Checks if immediate value can be converted to imm12(12 bits) value.
     */
    public static boolean imm8m(int x)
    {
        int rot;

            for (rot = 0; rot < 16; rot++)
                if ((x & ~ror32(0xff, 2 * rot)) == 0)
                    return true;

        return false;
    }

    public static boolean negImm8m(int x)
    {
        int rot;
        x = -x;
        for (rot = 0; rot < 16; rot++)
            if ((x & ~ror32(0xff, 2 * rot)) == 0)
                return true;

        return false;
    }

    public static boolean imm12(long x)
    {
        if(x>=0)
            return (((int)x)&0xfffff000) == 0;
        else
            return (((int)x)&0xfffff000) == 0xfffff000;
    }
/*
    public static void dealIfNotImm12(int num, Reg rd, AsmBuilder builder, FunctionDataHolder dataHolder)
    {
        if(!imm12(num))
        {
            dataHolder.addAndLoadFromFuncData(builder,num,rd);
        }
    }*/


    /**
     * 别用，这是有问题的
     */
    public static List<Integer> decompositionToImm12(int num)
    {
        List<Integer> result = new ArrayList<>();
        int remain = num;
        if(num>0)
        {
            while ((remain & 0xfffff000) !=0)
            {
                remain-=0x00000fff;
                result.add(0x00000fff);
            }
            result.add(remain);
        }else{
            while ((remain & 0xfffff000) !=0)
            {
                remain+=0x00000fff;
                result.add(0x00000fff);
            }
            result.add(remain);
        }
        return result;
    }

    public static void protectRegs(AsmBuilder builder,RegGetter regGetter,List<Reg> regs)
    {
        if(regs.size()>0)
        {
            Reg tmp = regGetter.getTmpRegister();
            //dataHolder.loadFromFuncData(builder, FunctionDataHolder.RegFuncData.getInstance(),tmp);
            builder.add(tmp,Regs.FP,AsmUtil.getRegOffsetFP());
            builder.stm(AsmBuilder.LSAddressMode.NONE,tmp,regs);
            regGetter.releaseReg(tmp);
        }
    }

    public static void recoverRegs(AsmBuilder builder,RegGetter regGetter,List<Reg> regs)
    {
        if(regs.size()>0)
        {
            Reg tmp = regGetter.getTmpRegister();
            //dataHolder.loadFromFuncData(builder, FunctionDataHolder.RegFuncData.getInstance(),tmp);

            builder.add(tmp,Regs.FP,AsmUtil.getRegOffsetFP());
            builder.ldm(AsmBuilder.LSAddressMode.NONE,tmp,regs);
            regGetter.releaseReg(tmp);
        }
    }
}
