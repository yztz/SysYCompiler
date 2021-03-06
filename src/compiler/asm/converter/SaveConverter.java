package compiler.asm.converter;

import compiler.LazyGetter;
import compiler.asm.*;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LSRepresent;
import compiler.genir.code.SaveRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class SaveConverter extends LSConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof SaveRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        SaveRepresent saveIR = (SaveRepresent)ir;

        RegGetter.LoadSaveInfo loadSaveInfo = regGetter.getLoadSaveInfo(funcSymbol);
        loadSaveInfo.putCanLastLoadedUse(saveIR.valueSymbol,saveIR.offset,false);


        LazyGetter<Reg> rdGetter;
        if(saveIR.target.isData)
        {
            Reg rd = regGetter.getTmpRegister(1);
            builder.mov(rd,saveIR.target.item);
            rdGetter = new LazyGetter<>(()->rd);
        }else{
            rdGetter = new LazyGetter<>(()->regGetter.distributeReg(ir, saveIR.target));
        }

        int num =  super.process(AsmBuilder.Mem.STR, builder, regGetter, (LSRepresent) ir,funcSymbol,dataHolder,
                                rdGetter);

        loadSaveInfo.putCanLastSaveUse(saveIR.valueSymbol,saveIR.offset,true);
        loadSaveInfo.putLastSaveAddress(saveIR.valueSymbol,saveIR.offset,saveIR.target);
        loadSaveInfo.putLastSaveReg(saveIR.valueSymbol,saveIR.offset,rdGetter.get());

        return num;
    }
}
