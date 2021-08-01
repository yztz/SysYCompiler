package compiler.asm;

import compiler.ConstDef;
import compiler.genir.code.AddressOrData;
import compiler.symboltable.HasInitSymbol;
import compiler.symboltable.ValueSymbol;
import compiler.symboltable.function.FuncSymbol;
import compiler.symboltable.initvalue.SingleInitValue;

import java.awt.*;
import java.util.*;
import java.util.List;

public class FunctionDataHolder {

    private final FuncSymbol funcSymbol;
    private final int id;
    private final String label;
    public int getId() {
        return id;
    }

    public FunctionDataHolder(FuncSymbol funcSymbol, int id) {
        this.funcSymbol = funcSymbol;
        this.id = id;
        label = getFuncDataLabel(funcSymbol,id);
    }
    private static String getFuncDataLabel(FuncSymbol funcSymbol,int id)
    {
        return String.format(".%s.data.%d",funcSymbol.getFuncName(),id);
    }

    public String getLabel() {
        return label;
    }
    // 只能加不能删
    private Map<FuncData,Integer> datas=new HashMap<>();
    private List<FuncData> dataList = new ArrayList<>();

    int currentIndex = 0;
    public void addData(FuncData data)
    {
        dataList.add(data);
        datas.put(data,currentIndex++);
    }
    public void addData(ValueSymbol symbol)
    {
        SymbolFuncData symbolFuncData = new SymbolFuncData(symbol);
        if(datas.containsKey(symbolFuncData)) //不重复添加
            return;
        dataList.add(currentIndex,symbolFuncData);
        datas.put(symbolFuncData,currentIndex++);
    }

    public void addData(int imm)
    {
        ImmFuncData immFuncData = new ImmFuncData(imm);
        if(datas.containsKey(immFuncData)) //不重复添加
            return;
        dataList.add(currentIndex,immFuncData);
        datas.put(immFuncData,currentIndex++);
    }

    /*public Set<Map.Entry<FuncData,Integer>> getDataAndIndex()
    {
        return datas.entrySet();
    }*/

    public List<FuncData> getAllFuncData()
    {
        return dataList;
    }

    public int getDataItemNum()
    {
        return datas.size();
    }


    public void addAndLoadFromFuncData(AsmBuilder builder,int num,Reg rd)
    {
        addData(num);
        loadFromFuncData(builder,num,rd);
    }
    public void loadFromFuncData(AsmBuilder builder,FuncData data,Reg rd)
    {
        int index = getIndexInFuncData(data);
        int offsetInFuncData = index* ConstDef.WORD_SIZE;
        builder.ldr(rd,label,offsetInFuncData);
    }
    public void loadFromFuncData(AsmBuilder builder,int num,Reg rd)
    {
        int index = getIndexInFuncData(num);
        int offsetInFuncData = index* ConstDef.WORD_SIZE;
        builder.ldr(rd,label,offsetInFuncData);
    }

    public void loadFromFuncData(AsmBuilder builder,ValueSymbol symbol,Reg rd)
    {
        int index = getIndexInFuncData(symbol);
        int offsetInFuncData = index* ConstDef.WORD_SIZE;
        builder.ldr(rd,label,offsetInFuncData);
    }
    public int getIndexInFuncData(FuncData data)
    {
        return datas.getOrDefault(data,0);
    }
    public int getIndexInFuncData(int num)
    {
        return datas.getOrDefault(new ImmFuncData(num),0);
    }

    public int getIndexInFuncData(ValueSymbol symbol)
    {
        return datas.getOrDefault(new SymbolFuncData(symbol),0);
    }

    public static class SymbolFuncData extends FuncData{
        ValueSymbol symbol;

        public SymbolFuncData(ValueSymbol symbol) {
            this.symbol = symbol;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SymbolFuncData that = (SymbolFuncData) o;
            return symbol.equals(that.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(symbol);
        }

        @Override
        public void genData(AsmBuilder builder) {

            if (symbol instanceof HasInitSymbol && ((HasInitSymbol)symbol).isGlobalSymbol()) {
                HasInitSymbol init = (HasInitSymbol) symbol;
                builder.word(init.asmDataLabel);
            }else{
                if (symbol instanceof HasInitSymbol) {
                    HasInitSymbol varSymbol = (HasInitSymbol) symbol;
                    if(AsmUtil.isNeedInitInDataSection(varSymbol))
                    {
                        builder.word(varSymbol.asmDataLabel);
                    }else if(varSymbol.initValues instanceof SingleInitValue){
                        builder.word(varSymbol.initValues.get(0));
                    }else{
                        builder.space(ConstDef.WORD_SIZE);
                    }
                }else{
                    builder.space(ConstDef.WORD_SIZE);
                }
            }
        }
    }
    public abstract static class FuncData{
        public abstract void genData(AsmBuilder builder);
    }
    public static class ImmFuncData extends FuncData{
        int imm32;

        public ImmFuncData(int imm32) {
            this.imm32 = imm32;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImmFuncData that = (ImmFuncData) o;
            return imm32 == that.imm32;
        }

        @Override
        public int hashCode() {
            return Objects.hash(imm32);
        }

        @Override
        public void genData(AsmBuilder builder) {
            builder.word(imm32);
        }
    }

    public static class RegFuncData extends FuncData{
        public static final String regDataLabel = "._reg";
        private static RegFuncData instance = null;
        public static RegFuncData getInstance(){
            if(instance==null)
                instance=new RegFuncData();

            return instance;
        }

        private RegFuncData(){}

        @Override
        public void genData(AsmBuilder builder) {
            builder.word(regDataLabel);
        }
    }
}
