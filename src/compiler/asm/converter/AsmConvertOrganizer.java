package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.RegGetter;
import compiler.genir.IRBlock;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.FuncSymbol;

import java.util.ArrayList;
import java.util.List;

public class AsmConvertOrganizer {
    public static List<AsmConverter> allConverter = new ArrayList<>();
    static {
        init();
    }
    public static void init()
    {
        allConverter.add(new BinocularConverter());
        allConverter.add(new LoadConverter());
        allConverter.add(new SaveConverter());
        allConverter.add(new GotoConverter());
        allConverter.add(new IfGotoConverter());
        allConverter.add(new ReturnConverter());
        allConverter.add(new CallConverter());
        allConverter.add(new InitVarConverter());
        allConverter.add(new LAddrConverter());
    }

    public static void process(AsmBuilder builder, RegGetter regGetter, FuncSymbol funcSymbol, IRBlock irBlock)
    {
        List<InterRepresent> flatIRList = irBlock.flatIR();
        for (int j = 0; j < flatIRList.size(); j++) {
            InterRepresent ir = flatIRList.get(j);
            for (AsmConverter converter : allConverter) {
                if (!converter.needProcess(ir, flatIRList, j)) continue;

                if (ir.hasLabel()) {
                    builder.label(ir.getLabel());
                }
                j+=converter.process(builder, regGetter, ir, flatIRList, j, funcSymbol)-1;

                break;
            }
        }
    }
}
