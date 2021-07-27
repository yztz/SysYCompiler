package compiler.asm.converter;

import compiler.ConstDef;
import compiler.asm.*;
import compiler.asm.operand.ShiftOp;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LSRepresent;
import compiler.genir.code.LoadRepresent;
import compiler.genir.code.SaveRepresent;
import compiler.symboltable.FuncSymbol;
import compiler.symboltable.ParamSymbol;
import compiler.symboltable.ValueSymbol;

import java.util.Collection;
import java.util.List;

public class SaveConverter extends LSConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof SaveRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        SaveRepresent saveIR = (SaveRepresent)ir;

        Reg rd;
        if(saveIR.target.isData)
        {
            rd = regGetter.getTmpRegister();
            builder.mov(rd,saveIR.target.item);
        }else{
            rd = regGetter.getReg(ir, saveIR.target);
        }

        return super.process(AsmBuilder.Mem.STR, builder, regGetter, (LSRepresent) ir,funcSymbol,rd);
    }
}
