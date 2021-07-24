package asm;

import genir.AbstractIR;
import genir.IRFunction;
import genir.IRUnion;
import symboltable.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AsmGen {
    SymbolTableHost symbolTableHost;

    public AsmGen(SymbolTableHost symbolTableHost) {
        this.symbolTableHost = symbolTableHost;
    }

    public String generate(IRUnion irUnion)
    {
        StringBuilder builder = new StringBuilder();

        List<AsmSection> staticDataSection = genStaticData();
        for (AbstractIR ir : irUnion.getAll()) {
            if (ir instanceof IRFunction) {
                AsmSection funcSection = genFunction((IRFunction) ir);
                funcSection.getText(builder);
            }
        }

        for (AsmSection dataSection : staticDataSection) {
            dataSection.getText(builder);
        }

        return builder.toString();
    }

    public AsmSection genFunction(IRFunction irFunction)
    {
        FuncSymbol funcSymbol = irFunction.funcSymbol;
        AsmBuilder asmBuilder = new AsmBuilder(funcSymbol.getAsmLabel());
        asmBuilder.text().align(2).global().arch("armv7-a").fpu("vfp").type(AsmBuilder.Type.Function).label();

        genFunctionGenericStart(asmBuilder,funcSymbol);

        asmBuilder.mov(Regs.R0,5);
        // todo 汇编代码生成

        genFunctionGenericEnd(asmBuilder,funcSymbol);

        return asmBuilder.getBuild();
    }

    //现场保护等
    public void genFunctionGenericStart(AsmBuilder asmBuilder,FuncSymbol funcSymbol)
    {
        if(funcSymbol.hasFuncCallInside())
        {
            asmBuilder.pop(new Reg[]{Regs.FP},true)
                        .add(Regs.FP,Regs.SP,4)
                        .sub(Regs.SP,Regs.SP,funcSymbol.getFrameSize());
        }else{
            //str fp,[sp,#-4]!
            //add fp,sp,#0,
            //sub sp,sp,#20
            asmBuilder.mem(AsmBuilder.Mem.STR, null, Regs.FP, Regs.SP, -4, true, false);
            asmBuilder.add(Regs.FP,Regs.SP,0);
            asmBuilder.sub(Regs.SP,Regs.SP, funcSymbol.getFrameSize()+4);
        }
    }

    //函数返回之类的指令
    public void genFunctionGenericEnd(AsmBuilder asmBuilder,FuncSymbol funcSymbol)
    {
        if(funcSymbol.hasFuncCallInside()){
            asmBuilder.sub(Regs.SP,Regs.FP,-4)
                        .pop(new Reg[]{Regs.FP},true);
        }else{
            //恢复sp,fp,跳转回调用处
            asmBuilder.add(Regs.SP,Regs.FP,0)
                    .mem(AsmBuilder.Mem.LDR,null,Regs.FP,Regs.SP,4,false,true)
                    .bx(Regs.LR);
        }
    }

    public List<AsmSection> genStaticData()
    {
        List<AsmSection> sections=new ArrayList<>();
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

                    sections.add(builder.getBuild());
                }else if(symbol instanceof ConstSymbol)
                {

                }
            }
        }

        return sections;
    }
}
