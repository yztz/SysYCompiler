package compiler.genir.code;

import antlr.SysYParser;
import org.antlr.v4.runtime.Token;
import compiler.symboltable.SymbolTableHost;
import compiler.symboltable.ValueSymbol;

import java.util.ArrayList;
import java.util.List;

public class ListenerUtil {
    public static class SymbolWithOffset{
        public ValueSymbol symbol;
        public AddressOrData offsetResult;
        public List<InterRepresent> irToCalculateOffset;

        public SymbolWithOffset(ValueSymbol symbol, AddressOrData offsetResult, List<InterRepresent> irToCalculateOffset) {
            this.symbol = symbol;
            this.offsetResult = offsetResult;
            this.irToCalculateOffset = irToCalculateOffset;
        }
    }

    /**
     * 获取符号和偏移量，这个偏移是数组下标对应的偏移
     * @param symbolTableHost 符号表
     * @param ctx LVal上下文，从语法树中获取
     * @return 符号和偏移量
     */
    public static SymbolWithOffset getSymbolAndOffset(SymbolTableHost symbolTableHost, SysYParser.LValContext ctx)
    {
        Token token = ctx.Identifier().getSymbol();
        ValueSymbol varSymbol = symbolTableHost.searchSymbol(ctx.domain, token);
        List<InterRepresent> irToCalculateOffset = new ArrayList<>();
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
                irToCalculateOffset.add(irAdd);
                offset = irAdd.target;
            }

            return new SymbolWithOffset(varSymbol, offset, irToCalculateOffset);

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
}
