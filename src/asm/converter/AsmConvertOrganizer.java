package asm.converter;

import asm.AsmBuilder;
import asm.RegGetter;
import genir.IRBlock;
import genir.code.InterRepresent;
import symboltable.FuncSymbol;

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
    }

    public static void process(AsmBuilder builder, RegGetter regGetter, FuncSymbol funcSymbol, IRBlock irBlock)
    {
        int i = 0;
        List<InterRepresent> flatIRList = irBlock.flatIR();
        for (InterRepresent ir :flatIRList) {
            for (AsmConverter converter : allConverter) {
                if(!converter.needProcess(ir,flatIRList,i))
                    continue;

                if(ir.hasLabel())
                {
                    builder.label(ir.getLabel());
                }
                converter.process(builder,regGetter , ir , flatIRList, i,funcSymbol );

                break;
            }
            i++;
        }
    }
}
