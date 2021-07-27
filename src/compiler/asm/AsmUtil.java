package compiler.asm;

import compiler.ConstDef;
import compiler.genir.code.*;
import compiler.symboltable.*;

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

    public static String getVarLabel(FuncSymbol funcSymbol, SymbolDomain domain, VarSymbol varSymbol)
    {
        return String.format(".L%s.%d.%s", funcSymbol == null ? "" : funcSymbol.getAsmLabel(),
                             domain.getId(), varSymbol.symbolToken.getText());
    }

    public static boolean isNeedInitInDataSection(VarSymbol varSymbol)
    {
        return varSymbol.hasConstInitValue && varSymbol.initValues.length>1;
    }
    public static String getFuncDataLabel(FuncSymbol funcSymbol)
    {
        return String.format(".%s.data",funcSymbol.funcName.getText());
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

    public static int getSymbolOffsetFp(ValueSymbol symbol)
    {
        return getSymbolOffset(symbol)-2* ConstDef.WORD_SIZE;
    }
    public static int getSymbolOffsetFp(ValueSymbol symbol, int arrayIndex)
    {
        return getSymbolOffset(symbol,arrayIndex) -2* ConstDef.WORD_SIZE;
    }

    public static int getParamOffsetCalledFp(int index)
    {
        return (index-4)*ConstDef.WORD_SIZE+4; //fp = sp-4
    }
    public static int getParamOffsetCallerSp(int index)
    {
        return (index-4)*ConstDef.WORD_SIZE;
    }
}
