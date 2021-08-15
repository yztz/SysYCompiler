package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.asm.operand.ShiftOp;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.LSRepresent;
import compiler.symboltable.HasInitSymbol;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.ParamSymbol;
import compiler.symboltable.ValueSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class LSConverter extends AsmConverter {

    protected static Map<ValueSymbol,Boolean> canLastLoadUse = new HashMap<>();
    protected static Map<ValueSymbol, AddressOrData> lastLoadAddress = new HashMap<>();

    public int process(AsmBuilder.Mem op, AsmBuilder builder, RegGetter regGetter, LSRepresent ir,
                       FuncSymbol funcSymbol, FunctionDataHolder data, Supplier<Reg> rdGetter) {
        ValueSymbol valueSymbol = ir.valueSymbol;

        if(!(valueSymbol instanceof ParamSymbol) || !valueSymbol.isArray())
        {
            Reg baseAddrReg;
            if(valueSymbol instanceof HasInitSymbol && ((HasInitSymbol)valueSymbol).isGlobalSymbol()) //是全局变量
            {
                /*int offsetInFuncData = valueSymbol.getIndexInFunctionData(funcSymbol)* ConstDef.WORD_SIZE;
                baseAddrReg = regGetter.getTmpRegister();
                builder.ldr(baseAddrReg, AsmUtil.getFuncDataLabel(funcSymbol), offsetInFuncData);*/

                baseAddrReg = regGetter.getTmpRegister();
                data.loadFromFuncData(builder,valueSymbol,baseAddrReg);

                if(ir.offset==null || ir.offset.isData)
                {
                    int offset = 0;
                    if(ir.offset!=null)
                        offset = AsmUtil.getSymbolArrayIndexOffset(ir.offset.item);

                    builder.mem(op, null, rdGetter.get(), baseAddrReg,
                                offset, false, false);
                }else{

                    //int offsetFPWord = AsmUtil.getSymbolOffset(valueSymbol)/ ConstDef.WORD_SIZE;
                    Reg rm = regGetter.distributeReg(ir, ir.offset);
                    //builder.add(rm,rm,offsetFPWord);
                    builder.mem(op, null, rdGetter.get(), baseAddrReg,
                                rm, false, ShiftOp.LSL, 2, false, false);
                }
            }else{ //局部变量，非数组参数
                baseAddrReg = Regs.FP;
                if(ir.offset==null || ir.offset.isData)
                {
                    int offset = 0;
                    if(ir.offset==null)
                        offset = AsmUtil.getSymbolOffsetFp(valueSymbol);
                    else
                        offset = AsmUtil.getSymbolOffsetFp(valueSymbol, ir.offset.item);

                    builder.mem(op, null, rdGetter.get(), baseAddrReg,
                                offset, false, false);
                }else{

                    int offsetFPWord = AsmUtil.getSymbolOffsetFp(valueSymbol)/ ConstDef.WORD_SIZE;
                    Reg rm = regGetter.distributeReg(ir, ir.offset);
                    builder.add(rm,rm,offsetFPWord);
                    builder.mem(op, null, rdGetter.get(), baseAddrReg,
                                rm, false, ShiftOp.LSL, 2, false, false);
                }
            }
        }else{//是函数参数，并且是数组，此时栈里保存的是地址

            if(ir.offset==null || ir.offset.isData)
            {
                int offsetFP = AsmUtil.getSymbolOffsetFp(valueSymbol);

                Reg tmp = regGetter.getTmpRegister();
                builder.ldr(tmp,Regs.FP,offsetFP); // 先读取地址

                int offsetArray = 0;
                if(ir.offset!=null && ir.offset.item!=0)
                {
                    offsetArray = ir.offset.item*ConstDef.WORD_SIZE;
                }
                builder.mem(op, null, rdGetter.get(), tmp,
                            offsetArray, false, false);
            }else{
                int offsetFP = AsmUtil.getSymbolOffsetFp(valueSymbol);
                Reg tmp = regGetter.getTmpRegister();
                builder.ldr(tmp,Regs.FP,offsetFP); // 先读取地址

                Reg rm = regGetter.distributeReg(ir, ir.offset);

                if(tmp==rm)
                {
                    System.exit(-2);
                    System.err.println("Base address reg equal to offset address :"+ir);
                }

                builder.mem(op, null, rdGetter.get(), tmp,
                            rm, false, ShiftOp.LSL, 2, false, false);
            }
        }





        return 1;
    }
}
