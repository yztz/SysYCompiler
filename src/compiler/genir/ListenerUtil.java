package compiler.genir;

import antlr.SysYParser;
import compiler.asm.AddressRWInfo;
import compiler.genir.IRCollection;
import compiler.genir.code.*;
import compiler.symboltable.HasInitSymbol;
import compiler.symboltable.ParamSymbol;
import org.antlr.v4.runtime.Token;
import compiler.symboltable.SymbolTableHost;
import compiler.symboltable.ValueSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerUtil {
    public static class IrCalOffset extends IRCollection{
        public AddressOrData offsetResult;

        public IrCalOffset(AddressOrData offsetResult,List<InterRepresent> irs) {
            this.offsetResult = offsetResult;
            this.irs.addAll(irs);
        }
    }
    public static class SymbolWithOffset<T extends ValueSymbol>{
        public T symbol;
        private AddressOrData offsetResult;
        private IRCollection irToCalculateOffset;

        public SymbolWithOffset(T symbol, AddressOrData offsetResult, IRCollection irToCalculateOffset) {
            this.symbol = symbol;
            this.irToCalculateOffset = irToCalculateOffset;
            this.offsetResult = offsetResult;
        }
        public boolean isOffsetImm()
        {
            return offsetResult.isData;
        }
        public int getOffsetImm()
        {
            return offsetResult.item;
        }
        public IrCalOffset getOffsetCalculatorGroup() {
            List<InterRepresent> copyIrs = new ArrayList<>();
            List<InterRepresent> allIR = irToCalculateOffset.getAllIR();
            Map<AddressOrData,AddressOrData> replacedAddress = new HashMap<>();
            for (InterRepresent ir : allIR) {
                if(ir instanceof WrittenRepresent)
                {
                    AddressOrData oldAddress = ((WrittenRepresent) ir).target;
                    if(!replacedAddress.containsKey(oldAddress))
                    {
                        replacedAddress.put(oldAddress,AddressOrData.createNewAddress());
                    }
                }
                InterRepresent copyIR = ir.createCopy();
                for (AddressRWInfo rwInfo : copyIR.getAllAddressRWInfo()) {
                    if(replacedAddress.containsKey(rwInfo.address))
                    {
                        AddressOrData ad = replacedAddress.get(rwInfo.address);
                        rwInfo.address.item = ad.item;
                        rwInfo.address.isData = ad.isData;
                    }
                }
                copyIrs.add(copyIR);
            }

            AddressOrData newResult = replacedAddress.get(offsetResult);
            if(newResult==null)
                newResult = offsetResult;
            return new IrCalOffset(newResult,copyIrs);
        }
    }

    /**
     * 获取符号和偏移量，这个偏移是数组下标对应的偏移
     * @param ctx LVal上下文，从语法树中获取
     * @return 符号和偏移量
     */
    public static SymbolWithOffset<HasInitSymbol> getSymbolAndOffset(HasInitSymbol varSymbol, SysYParser.LValContext ctx)
    {
        IRCollection irToCalculateOffset = new IRCollection();
        if(varSymbol==null){
            //System.err.println("Symbol is not defined");
            return null;
        }
        else {
            int[] dimensions = varSymbol.dimensions;
            int[] dimSizes = new int[dimensions.length];
            for (int i = 0; i < dimensions.length; i++) {
                dimSizes[i] = 1;
                for (int j = i + 1; j < dimensions.length; j++) {
                    dimSizes[i] *= dimensions[j];
                }
            }

            int offsetConst = 0;
            AddressOrData lastResult = null;

            //数组的处理
            for (int i = 0; i < ctx.exp().size(); i++) {
                AddressOrData indexInDim = ctx.exp(i).result;
                if (!indexInDim.isData) //是变量
                {
                    // 计算当前维度偏移了多少
                    BinocularRepre irMul = InterRepresentFactory.createBinocularRepresent(BinocularRepre.Opcodes.MUL,
                                                                                          new AddressOrData(true,
                                                                                                            dimSizes[i]),
                                                                                          indexInDim);
                    irToCalculateOffset.addCode(irMul,ctx.exp(i).start);
                    if (lastResult != null) {
                        BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(
                                BinocularRepre.Opcodes.ADD, lastResult, irMul.target);
                        irToCalculateOffset.addCode(irAdd,null);
                        lastResult = irAdd.target;
                    } else {
                        lastResult = irMul.target;
                    }
                } else { //是常量
                    offsetConst += dimSizes[i] * indexInDim.item;
                }
            }
            AddressOrData offset;
            if (lastResult == null) // 全都是常量的情况下
            {
                offset = new AddressOrData(true, offsetConst);
            } else if (offsetConst == 0) //全是变量的情况下
            {
                offset = lastResult;
            } else { //变量和常量混合
                BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(BinocularRepre.Opcodes.ADD,
                                                                                      lastResult, new AddressOrData(true,
                                                                                                                    offsetConst));
                irToCalculateOffset.addCode(irAdd,null);
                offset = irAdd.target;
            }

            return new SymbolWithOffset<>(varSymbol, offset, irToCalculateOffset);

        }
    }


    /**
     * 获取符号和偏移量，这个偏移是数组下标对应的偏移
     * @param ctx LVal上下文，从语法树中获取
     * @return 符号和偏移量
     */
    public static SymbolWithOffset<ParamSymbol> getSymbolAndOffset(ParamSymbol varSymbol, SysYParser.LValContext ctx)
    {
        IRCollection irToCalculateOffset = new IRCollection();
        if(varSymbol==null){
            //System.err.println("Symbol is not defined");
            return null;
        }
        else {
            ParamSymbol.IrCalDim dimCalculator = varSymbol.getDimCalculator();
            irToCalculateOffset.addCodes(dimCalculator);
            AddressOrData[] dimensions = dimCalculator.dimensions;
            AddressOrData[] sizeInDim = new AddressOrData[dimensions.length];
            /*for (int i = 0; i < dimensions.length; i++) {
                sizeInDim[i] = 1;
                for (int j = i + 1; j < dimensions.length; j++) {
                    sizeInDim[i] *= dimensions[j];
                }
            }*/

            sizeInDim[sizeInDim.length-1]=new AddressOrData(true,1);

            AddressOrData lastResult = null;
            for(int i = ctx.exp().size()-1;i>=0;i--)
            {
                AddressOrData indexInDim = ctx.exp(i).result;
                /*if (!indexInDim.isData) //是变量
                {*/
                    // 计算当前维度偏移了多少
                    if(i>=0 && i!=ctx.exp().size()-1)
                    {
                        BinocularRepre dimSizeCal = InterRepresentFactory.createBinocularRepresent(BinocularRepre.Opcodes.MUL,
                                                                                                   sizeInDim[i+1],
                                                                                                   dimensions[i+1]);
                        sizeInDim[i] = dimSizeCal.target;
                        irToCalculateOffset.addCode(dimSizeCal,null);
                    }

                    BinocularRepre irMul = InterRepresentFactory.createBinocularRepresent(BinocularRepre.Opcodes.MUL,
                                                                                          sizeInDim[i],
                                                                                          indexInDim);
                    irToCalculateOffset.addCode(irMul,ctx.exp(i).start);
                    if (lastResult != null) {
                        BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(
                                BinocularRepre.Opcodes.ADD, lastResult, irMul.target);
                        irToCalculateOffset.addCode(irAdd,null);
                        lastResult = irAdd.target;
                    } else {
                        lastResult = irMul.target;
                    }


                /*} else { //是常量
                    offsetConst += sizeInDim[i] * indexInDim.item;
                }*/
            }


            /*int offsetConst = 0;
            AddressOrData lastResult = null;

            //数组的处理
            for (int i = 0; i < ctx.exp().size(); i++) {
                AddressOrData indexInDim = ctx.exp(i).result;
                if (!indexInDim.isData) //是变量
                {
                    // 计算当前维度偏移了多少
                    BinocularRepre irMul = InterRepresentFactory.createBinocularRepresent(BinocularRepre.Opcodes.MUL,
                                                                                          new AddressOrData(true,
                                                                                                            sizeInDim[i]),
                                                                                          indexInDim);
                    irToCalculateOffset.add(irMul);
                    if (lastResult != null) {
                        BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(
                                BinocularRepre.Opcodes.ADD, lastResult, irMul.target);
                        irToCalculateOffset.add(irAdd);
                        lastResult = irAdd.target;
                    } else {
                        lastResult = irMul.target;
                    }
                } else { //是常量
                    offsetConst += sizeInDim[i] * indexInDim.item;
                }
            }
            AddressOrData offset;
            if (lastResult == null) // 全都是常量的情况下
            {
                offset = new AddressOrData(true, offsetConst);
            } else if (offsetConst == 0) //全是变量的情况下
            {
                offset = lastResult;
            } else { //变量和常量混合
                BinocularRepre irAdd = InterRepresentFactory.createBinocularRepresent(BinocularRepre.Opcodes.ADD,
                                                                                      lastResult, new AddressOrData(true,
                                                                                                                    offsetConst));
                irToCalculateOffset.add(irAdd);
                offset = irAdd.target;
            }*/
            AddressOrData offset;
            if(lastResult==null)
            {
                offset=new AddressOrData(true,0);
            }else{
                offset= lastResult;
            }
            return new SymbolWithOffset<>(varSymbol, offset, irToCalculateOffset);

        }
    }


    public static  int[] getDimsFromConstExp(List<SysYParser.ConstExpContext> expCtxList) {
        int[] dims= new int[expCtxList.size()];
        for (int i = 0; i < expCtxList.size(); i++) {
            if (expCtxList.get(i).result!=null&&
                    expCtxList.get(i).result.isData) {
                dims[i]= expCtxList.get(i).result.item;
            }else{
                dims[i]=1;
                System.err.println("Array size must be constant");
            }
        }
        return dims;
    }
    public static int getLengthFromDimensions(int[] dimensions)
    {
        int length = 1;
        for (int dim : dimensions) {
            length*=dim;
        }
        return length;
    }
}
