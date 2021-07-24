package asm.converter;

import asm.AsmBuilder;
import asm.Regs;
import genir.code.InterRepresent;
import genir.code.LoadRepresent;
import symboltable.VarSymbol;

import java.util.Collection;

public class LoadConverter extends AsmConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof LoadRepresent;
    }

    @Override
    public void process(AsmBuilder builder, InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        LoadRepresent loadIR = (LoadRepresent) ir;
        VarSymbol varSymbol = loadIR.varSymbol;
        loadIR.target.reg = Regs.R2;

        builder.mem(AsmBuilder.Mem.LDR,null,loadIR.target.reg,Regs.SP,-(8+varSymbol.offset),false,false);
    }
}
