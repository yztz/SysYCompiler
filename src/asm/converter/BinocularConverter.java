package asm.converter;

import asm.AsmBuilder;
import asm.Reg;
import asm.RegGetter;
import asm.operand.ImmOperand;
import asm.operand.RegOperand;
import genir.code.BinocularRepre;
import genir.code.InterRepresent;
import symboltable.FuncSymbol;

import java.util.Collection;

public class BinocularConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof BinocularRepre;
    }

    @Override
    public void process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, Collection<InterRepresent> allIR, int index, FuncSymbol funcSymbol) {
        BinocularRepre bIR = (BinocularRepre) ir;
        if (bIR.sourceFirst.isData && bIR.sourceSecond.isData) { //都是立即数，在IR生成阶段这个应该就被处理掉了
            int result = 0;
            int s1 = bIR.sourceFirst.item;
            int s2 = bIR.sourceSecond.item;
            switch (bIR.OP) {

                case ADD:
                    result = s1+s2;
                    break;
                case MINUS:
                    result = s1-s2;
                    break;
                case MUL:
                    result = s1*s2;
                    break;
                case DIV:
                    result = s1/s2;
                    break;
                case MOD:
                    result = s1%s2;
                    break;
            }
            Reg reg = regGetter.getRegOfAddress(ir,bIR.target);
            builder.mov(reg,result);
            bIR.target.reg = reg;
            //todo 分配哪个寄存器保存到AddressOrData里面了，有点乱，其实也可以不保存每一次都用getRegOfAddress获取
            return ;
        }


        Reg reg = regGetter.getRegOfAddress(ir, bIR.target);
        bIR.target.reg = reg;

        if(bIR.OP== BinocularRepre.Opcodes.ADD || bIR.OP== BinocularRepre.Opcodes.MINUS)
        {
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                AsmBuilder.RegRegOperandOP op = bIR.OP == BinocularRepre.Opcodes.ADD ? AsmBuilder.RegRegOperandOP.ADD : AsmBuilder.RegRegOperandOP.SUB;

                builder.regRegOperand(op, reg, bIR.sourceFirst.reg, new RegOperand(bIR.sourceSecond.reg));
            }else if(!bIR.sourceFirst.isData){
                if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                    builder.add(reg, bIR.sourceFirst.reg, bIR.sourceSecond.item);
                }else{
                    builder.sub(reg, bIR.sourceFirst.reg, bIR.sourceSecond.item);
                }
            }else{
                if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                    builder.add(reg, bIR.sourceFirst.reg, bIR.sourceSecond.item);
                }else{
                    builder.regRegOperand(AsmBuilder.RegRegOperandOP.RSB,
                                          reg,
                                          bIR.sourceFirst.reg,
                                          new ImmOperand(bIR.sourceSecond.item));
                }
            }
        }else{
            Reg rd,rn;
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                rd = bIR.sourceFirst.reg;
                rn = bIR.sourceSecond.reg;
            }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                rd = bIR.sourceFirst.reg;
                rn = regGetter.getTmpRegister();
                builder.mov(rn, bIR.sourceSecond.item);
            }else{
                rd = regGetter.getTmpRegister();
                rn = bIR.sourceSecond.reg;
                builder.mov(rd,bIR.sourceSecond.item);
            }

            AsmBuilder.TripleRegOP op = null;
            switch (bIR.OP)
            {
                case MUL:
                    op = AsmBuilder.TripleRegOP.MUL;
                    break;
                case DIV:
                    op = AsmBuilder.TripleRegOP.SDIV;
                    break;
                case MOD:
                    // todo 貌似没有取余的指令
                    break;
            }
            builder.tripleReg(op, reg, rd, rn);

        }
    }
}
