package asm.converter;

import asm.AsmBuilder;
import genir.IRFunction;
import genir.code.InterRepresent;

import java.util.ArrayList;
import java.util.LinkedList;
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
    }

    public static void process(AsmBuilder builder,IRFunction irFunction)
    {
        int i = 0;
        LinkedList<InterRepresent> flatIRList = irFunction.flatIR();
        for (InterRepresent ir :flatIRList) {
            for (AsmConverter converter : allConverter) {
                if(!converter.needProcess(ir,flatIRList,i))
                    continue;

                converter.process(builder,ir,flatIRList,i);

                break;
            }
            i++;
        }
    }
}
