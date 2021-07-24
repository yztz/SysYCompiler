package asm.converter;

import asm.AsmBuilder;
import asm.RegGetter;
import genir.code.InterRepresent;
import symboltable.FuncSymbol;

import java.util.Collection;

public abstract class AsmConverter {
    public abstract boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index);

    public abstract void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol);
}
