package compiler.asm.converter;

import compiler.asm.*;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LAddrRepresent;
import compiler.symboltable.FuncSymbol;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;
import java.util.List;

public class LAddrConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof LAddrRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        LAddrRepresent retIr = (LAddrRepresent) ir;
        Reg rd = regGetter.getReg(retIr, retIr.target);
        ValueSymbol symbol = retIr.valueSymbol;
        int symbolOffsetFp = Util.getSymbolOffsetFp(symbol);
        builder.add(rd,Regs.FP,symbolOffsetFp);
        return 1;
    }
}
