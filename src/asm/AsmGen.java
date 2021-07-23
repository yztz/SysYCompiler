package asm;

import symboltable.*;

public class AsmGen {
    SymbolTableHost symbolTableHost;

    public static String asmTemplate =
            ".arch armv7\r\n" +
            "%s" + //.section rodata
            "%s"; //function



    public void genStaticData()
    {
        for (SymbolTable table : symbolTableHost.symbolTableMap.values()) {
            SymbolDomain domain  = table.getDomain();
            FuncSymbol funcSymbol = domain.getFunc();
            for (ValueSymbol symbol : table.getAllSymbol()) {
                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    if (!varSymbol.hasConstInitValue) {
                        continue;
                    }

                    AsmBuilder builder = new AsmBuilder();
                    String label = String.format(".L%s.%d.%s", funcSymbol == null ? "" : funcSymbol.getAsmLabel(),
                                                 domain.getId(), varSymbol.symbolToken.getText());

                    varSymbol.asmDataLabel = label;

                    builder.type(label, AsmBuilder.Type.Object)
                            .data()
                            .globl(label)
                            .align(2)
                            .label(label);

                    for (int initValue : varSymbol.initValues) {
                        builder.word(initValue);
                    }
                    builder.size(label, varSymbol.getByteSize());
                }else if(symbol instanceof ConstSymbol)
                {

                }
            }
        }
    }
}
