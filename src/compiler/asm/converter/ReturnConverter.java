package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.RegGetter;
import compiler.asm.Regs;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.ReturnRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class ReturnConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof ReturnRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        ReturnRepresent retIr = (ReturnRepresent) ir;
        if (retIr.returnData != null) {
            if (retIr.returnData.isData) {
                builder.mov(Regs.R0, retIr.returnData.item);
            } else {
                builder.mov(Regs.R0, regGetter.getReg(ir,retIr.returnData));
            }
        }
        builder.b(funcSymbol.getAsmEndLabel());

        return 1;
    }
}
