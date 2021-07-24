package asm.converter;

import asm.AsmBuilder;
import asm.Regs;
import asm.operand.RegOperand;
import genir.code.AddressOrData;
import genir.code.BinocularRepre;
import genir.code.InterRepresent;

import java.util.Collection;

public class BinocularConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof BinocularRepre;
    }

    @Override
    public void process(AsmBuilder builder, InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        BinocularRepre bIR = (BinocularRepre) ir;
        if (bIR.sourceFirst.isData && bIR.sourceSecond.isData) { //在IR生成阶段这个应该就被处理掉了
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
            //todo 先随便放个寄存器吧
            builder.mov(Regs.R2,result);
            bIR.target.reg = Regs.R2;
            return ;
        }

        if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
        {
            if(bIR.OP== BinocularRepre.Opcodes.ADD || bIR.OP== BinocularRepre.Opcodes.MINUS)
            {
                AsmBuilder.RegRegOperandOP op = bIR.OP== BinocularRepre.Opcodes.ADD?AsmBuilder.RegRegOperandOP.ADD
                        :AsmBuilder.RegRegOperandOP.SUB;
                builder.regRegOperand(op, Regs.R2, bIR.sourceFirst.reg, new RegOperand(bIR.sourceSecond.reg));
            }else{
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
                builder.tripleReg(op, Regs.R2, bIR.sourceFirst.reg, bIR.sourceSecond.reg);

            }
        }
    }
}
