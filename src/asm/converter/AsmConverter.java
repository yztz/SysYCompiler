package asm.converter;

import asm.AsmBuilder;
import genir.AbstractIR;
import genir.IRFunction;
import genir.code.InterRepresent;

public abstract class AsmConverter {
    public abstract boolean needProcess(InterRepresent ir, AbstractIR irFunction, int index);

    public abstract void process(AsmBuilder builder);
}
