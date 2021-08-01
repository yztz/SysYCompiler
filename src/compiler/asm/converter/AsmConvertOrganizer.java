package compiler.asm.converter;

import compiler.asm.*;
import compiler.genir.IRBlock;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LAddrRepresent;
import compiler.genir.code.LSRepresent;
import compiler.symboltable.function.FuncSymbol;

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
        allConverter.add(new SaveConverter());
        allConverter.add(new GotoConverter());
        allConverter.add(new IfGotoConverter());
        allConverter.add(new ReturnConverter());
        allConverter.add(new CallConverter());
        allConverter.add(new InitVarConverter());
        allConverter.add(new LAddrConverter());
        allConverter.add(new UnaryConverter());
    }

    public static List<AsmSection> process(RegGetter regGetter, FuncSymbol funcSymbol, List<IRBlock> irBlocks)
    {
        List<AsmSection> asmSections = new LinkedList<>();
        AsmBuilder builder = new AsmBuilder(funcSymbol.getAsmLabel());


        int dataId = 0;
        FunctionDataHolder holder = new FunctionDataHolder(funcSymbol,dataId++);

        // 如果mov的立即数不是imm12，则替换成ldr,改为从内存中加载
        builder.hookIfNotImmXX(holder, regGetter);

        for (IRBlock irBlock : irBlocks) {
            List<InterRepresent> flatIRList = irBlock.flatIR();
            for (int j = 0; j < flatIRList.size(); j++) {
                InterRepresent ir = flatIRList.get(j);

                if(ir instanceof LSRepresent)
                {
                    holder.addData(((LSRepresent) ir).valueSymbol);
                }else if(ir instanceof LAddrRepresent)
                {
                    holder.addData(((LAddrRepresent) ir).valueSymbol);
                }

                for (AsmConverter converter : allConverter) {
                    if (!converter.needProcess(ir, flatIRList, j)) continue;

                    if (ir.hasLabel()) {
                        builder.label(ir.getLabel());
                    }
                    j+=converter.process(builder, regGetter, ir, flatIRList, j, funcSymbol,holder )-1;
                    regGetter.stepToNextIR();
                    //builder.commit(ir.toString());
                    break;
                }

                if(builder.totalLineNum()>1022)
                {
                    asmSections.add(builder.getSectionAndStartNew());
                    if (holder.getDataItemNum()>0) {
                        asmSections.add(genFunctionData(holder,false));
                        holder = new FunctionDataHolder(funcSymbol,dataId++);
                    }
                    // 如果mov的立即数不是imm12，则替换成ldr,改为从内存中加载
                    builder.hookIfNotImmXX(holder, regGetter);
                }
            }
        }

        asmSections.add(builder.getSectionAndStartNew());

        //开始新的AsmSection,生成函数的开头
        //把开头放在这里生成，是因为我们需要知道代码中是否修改了lr寄存器
        builder.text().align(2).global().arch("armv7-a").fpu("vfp").type(AsmBuilder.Type.Function).label();
        genFunctionGenericStart(builder, funcSymbol,regGetter);
        asmSections.add(0,builder.getSectionAndStartNew());


        genFunctionGenericEnd(builder, funcSymbol);
        asmSections.add(builder.getSection());

        if(holder.getDataItemNum()>0)
            asmSections.add(genFunctionData(holder,true));
        //regGetter.releaseAll();

        return asmSections;
    }

    public static AsmSection genFunctionData(FunctionDataHolder holder,boolean laseOne)
    {
        AsmBuilder builder = new AsmBuilder(holder.getLabel());

        if(!laseOne)
            builder.b(holder.getLabel()+".end");

        builder.label();
        for (FunctionDataHolder.FuncData data : holder.getAllFuncData()) {
            data.genData(builder);
        }
        if(!laseOne)
            builder.label(holder.getLabel()+".end");
        return builder.getSection();
    }

    //现场保护等
    public static void genFunctionGenericStart(AsmBuilder asmBuilder, FuncSymbol funcSymbol,RegGetter regGetter) {

        if (asmBuilder.isLrModified()) {
            asmBuilder.push(new Reg[]{Regs.FP}, true).add(Regs.FP, Regs.SP, 4).sub(Regs.SP, Regs.SP,
                                                                                   funcSymbol.getStackFrameSize()+4);
        } else {
            //str fp,[sp,#-4]!
            //add fp,sp,#0,
            //sub sp,sp,#20
            asmBuilder.mem(AsmBuilder.Mem.STR, null, Regs.FP, Regs.SP, -4, true, false);
            asmBuilder.add(Regs.FP, Regs.SP, 0);
            asmBuilder.sub(Regs.SP, Regs.SP, funcSymbol.getStackFrameSize() + 4);
        }

        for (int i = 0; i < Math.min(4,funcSymbol.getParamNum()); i++) {
            asmBuilder.str(Regs.REGS[i], Regs.FP, AsmUtil.getSymbolOffsetFp(funcSymbol.paramSymbols.get(i)));
        }
        for(int i = funcSymbol.getParamNum()-1;i>=4;i--)
        {
            Reg tmp = regGetter.getTmpRegister();
            asmBuilder.ldr(tmp, Regs.FP, AsmUtil.getParamOffsetCalledFp(i));
            asmBuilder.str(tmp, Regs.FP, AsmUtil.getSymbolOffsetFp(funcSymbol.paramSymbols.get(i)));
        }
    }

    //函数返回之类的指令
    public static void genFunctionGenericEnd(AsmBuilder asmBuilder, FuncSymbol funcSymbol) {

        asmBuilder.label(funcSymbol.getAsmEndLabel());
        if (asmBuilder.isLrModified()) {
            asmBuilder.sub(Regs.SP, Regs.FP, 4).pop(new Reg[]{Regs.FP}, true);
        } else {
            //恢复sp,fp,跳转回调用处
            asmBuilder.add(Regs.SP, Regs.FP, 0).mem(AsmBuilder.Mem.LDR, null, Regs.FP, Regs.SP, 4, false, true).bx(
                    Regs.LR);
        }
    }
}
