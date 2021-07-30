package compiler.asm.converter;

import compiler.asm.*;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LSRepresent;
import compiler.genir.code.SaveRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class SaveConverter extends LSConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof SaveRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        SaveRepresent saveIR = (SaveRepresent)ir;

        Reg rd;
        if(saveIR.target.isData)
        {
            rd = regGetter.getTmpRegister(1);
            builder.mov(rd,saveIR.target.item);
        }else{
            rd = regGetter.getReg(ir, saveIR.target);
        }

        return super.process(AsmBuilder.Mem.STR, builder, regGetter, (LSRepresent) ir,funcSymbol,dataHolder,rd);
    }
}
