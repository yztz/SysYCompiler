package asm.converter;

import asm.AsmBuilder;
import genir.AbstractIR;
import genir.IRFunction;
import genir.code.InterRepresent;

import java.util.Collection;

public abstract class AsmConverter {
    public abstract boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index);

    public abstract void process(AsmBuilder builder,InterRepresent ir,  Collection<InterRepresent> allIR, int index);
}
