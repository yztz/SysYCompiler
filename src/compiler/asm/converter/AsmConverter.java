package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.RegGetter;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;

public abstract class AsmConverter {
    public abstract boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index);

    public abstract int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol);
}
