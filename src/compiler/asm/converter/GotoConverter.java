package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.RegGetter;
import compiler.genir.code.GotoRepresent;
import compiler.genir.code.IfGotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class GotoConverter extends AsmConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof GotoRepresent && !(ir instanceof IfGotoRepresent);
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        GotoRepresent gotoIr = (GotoRepresent) ir;
        String targetLabel =gotoIr.targetHolder.getInterRepresent().getLabel();
        if(targetLabel==null)
        {
            System.out.println("找不到跳转目标:"+ gotoIr);
            return 1;
        }
        builder.b(targetLabel);

        return 1;
    }
}
