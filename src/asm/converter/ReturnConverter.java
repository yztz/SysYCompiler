package asm.converter;

import asm.AsmBuilder;
import asm.RegGetter;
import asm.Regs;
import genir.code.InterRepresent;
import genir.code.ReturnRepresent;
import symboltable.FuncSymbol;

import java.util.Collection;

public class ReturnConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof ReturnRepresent;
    }

    @Override
    public void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        ReturnRepresent retIr = (ReturnRepresent) ir;
        if (retIr.returnData != null) {
            if (retIr.returnData.isData) {
                builder.mov(Regs.R0, retIr.returnData.item);
            } else {
                builder.mov(Regs.R0, retIr.returnData.reg);
            }
        }
        builder.b(funcSymbol.getAsmEndLabel());
    }
}
