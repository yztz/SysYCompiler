package compiler.asm.converter;

import compiler.asm.*;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LSRepresent;
import compiler.genir.code.LoadRepresent;
import compiler.symboltable.*;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class LoadConverter extends LSConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof LoadRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        LoadRepresent loadIr = (LoadRepresent) ir;
        boolean flag = false;
        if (loadIr.valueSymbol instanceof ConstSymbol) {
            ConstSymbol symbol = (ConstSymbol) loadIr.valueSymbol;
            if(loadIr.offset==null)
            {
                builder.mov(regGetter.getReg(ir,loadIr.target),symbol.initValues[0]);
                flag = true;
            }else if(loadIr.offset.isData){
                builder.mov(regGetter.getReg(ir,loadIr.target),symbol.initValues[loadIr.offset.item]);
                flag = true;
            }
        }
        if(!flag)
            return super.process(AsmBuilder.Mem.LDR, builder, regGetter, (LSRepresent) ir,funcSymbol, dataHolder,
                             regGetter.getReg(ir, ((LSRepresent) ir).target));
        else
            return 1;
    }

}
