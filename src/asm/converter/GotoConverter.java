package asm.converter;

import asm.AsmBuilder;
import asm.RegGetter;
import genir.code.GotoRepresent;
import genir.code.IfGotoRepresent;
import genir.code.InterRepresent;
import symboltable.FuncSymbol;

import java.util.Collection;

public class GotoConverter extends AsmConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof GotoRepresent && !(ir instanceof IfGotoRepresent);
    }

    @Override
    public void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        GotoRepresent gotoIr = (GotoRepresent) ir;
        String targetLabel =gotoIr.targetHolder.getInterRepresent().getLabel();
        if(targetLabel==null)
        {
            System.out.println("找不到跳转目标:"+ gotoIr);
            return;
        }
        builder.b(targetLabel);
    }
}
