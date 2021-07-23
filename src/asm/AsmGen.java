package asm;

import genir.IRFunction;
import symboltable.*;

public class AsmGen {
    SymbolTableHost symbolTableHost;


    public void genFunction(IRFunction irFunction)
    {
        FuncSymbol funcSymbol = irFunction.funcSymbol;
        AsmBuilder asmBuilder = new AsmBuilder(funcSymbol.getAsmLabel());
        asmBuilder.text().align(2).global().arch("armv7").fpu("vfp").type(AsmBuilder.Type.Function).label();

        genFunctionGenericStart(asmBuilder,funcSymbol);

        // todo 汇编代码生成

        genFunctionGenericEnd(asmBuilder,funcSymbol);
    }

    //现场保护等
    public void genFunctionGenericStart(AsmBuilder asmBuilder,FuncSymbol funcSymbol)
    {
        //str fp,[sp,#-4]!
        //add fp,sp,#0,
        //sub sp,sp,#20
        asmBuilder.mem(AsmBuilder.Mem.STR, null, Regs.FP, Regs.SP, -4, true, false);
        asmBuilder.add(Regs.FP,Regs.SP,0);
        asmBuilder.sub(Regs.SP,Regs.SP,20); //todo 这里应该是帧大小,还没算，先填个12

    }

    //函数返回之类的指令
    public void genFunctionGenericEnd(AsmBuilder asmBuilder,FuncSymbol funcSymbol)
    {
        //恢复sp,fp,跳转回调用处
        asmBuilder.add(Regs.SP,Regs.FP,0)
                .mem(AsmBuilder.Mem.LDR,null,Regs.FP,Regs.SP,4,false,true)
                .bx(Regs.LR);
    }

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
                            .global(label)
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
