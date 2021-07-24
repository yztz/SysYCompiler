package asm.converter;

import asm.AsmBuilder;
import asm.RegGetter;
import asm.Regs;
import genir.code.InterRepresent;
import genir.code.SaveRepresent;
import symboltable.FuncSymbol;
import symboltable.ValueSymbol;

import java.util.Collection;

public class SaveConverter extends AsmConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof SaveRepresent;
    }

    @Override
    public void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        SaveRepresent loadIR = (SaveRepresent) ir;
        ValueSymbol varSymbol = loadIR.valueSymbol;

        loadIR.target.reg = regGetter.getRegOfAddress(ir, loadIR.target);

        builder.mem(AsmBuilder.Mem.STR, null, loadIR.target.reg, Regs.SP, -(8+varSymbol.getOffsetByte()), false, false);
    }
}
