package compiler.asm.converter;

import compiler.asm.AsmBuilder;
import compiler.asm.Reg;
import compiler.asm.RegGetter;
import compiler.asm.operand.ImmOperand;
import compiler.asm.operand.RegOperand;
import compiler.genir.code.BinocularRepre;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.UnaryRepre;
import compiler.symboltable.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class UnaryConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof UnaryRepre;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        UnaryRepre uIR = (UnaryRepre) ir;
        if (uIR.source.isData) { //是立即数，在IR生成阶段这个应该就被处理掉了
            int result = uIR.source.item;
            switch (uIR.OP) {

                case MINUS:
                    result = -result;
                    break;
                case ADD:
                    break;
                case NOT:
                    // todo 这个not是想干啥
                    break;
            }
            builder.mov(regGetter.getReg(ir,uIR.target),result);
        }else{
            Reg rd = regGetter.getReg(ir,uIR.target);
            switch (uIR.OP) {

                case MINUS:
                    Reg rn = regGetter.getReg(ir,uIR.source);
                    builder.sub(rn,rn,1);
                    builder.regOperand(AsmBuilder.RegOperandOP.MVN,rd,new RegOperand(rn));
                    break;
                case ADD:
                    //builder.mov(rd,new RegOperand(rd));
                    break;
                case NOT:
                    // todo 这个not是想干啥
                    break;
            }
        }

        return 1;
    }
}
