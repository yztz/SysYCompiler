package compiler.asm.converter;

import compiler.asm.*;
import compiler.asm.operand.ImmOperand;
import compiler.asm.operand.RegOperand;
import compiler.genir.code.BinocularRepre;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BinocularConverter extends AsmConverter{
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof BinocularRepre;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        BinocularRepre bIR = (BinocularRepre) ir;
        if (bIR.sourceFirst.isData && bIR.sourceSecond.isData) { //都是立即数，在IR生成阶段这个应该就被处理掉了
            int result = 0;
            int s1 = (int) bIR.sourceFirst.item;
            int s2 = (int) bIR.sourceSecond.item;
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
            Reg reg = regGetter.getReg(ir, bIR.target);

            //AsmUtil.dealIfNotImm12(result,reg,builder,dataHolder);

            builder.mov(reg,result);
            return 1;
        }


        Reg reg = regGetter.getReg(ir, bIR.target);

        if(bIR.OP== BinocularRepre.Opcodes.ADD || bIR.OP== BinocularRepre.Opcodes.MINUS)
        {
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                AsmBuilder.RegRegOperandOP op = bIR.OP == BinocularRepre.Opcodes.ADD ? AsmBuilder.RegRegOperandOP.ADD : AsmBuilder.RegRegOperandOP.SUB;

                builder.regRegOperand(op, reg, regGetter.getReg(ir,bIR.sourceFirst),
                                      new RegOperand(regGetter.getReg(ir,bIR.sourceSecond)));
            }else if(!bIR.sourceFirst.isData){
                Reg rn = regGetter.getReg(ir,bIR.sourceFirst);

                /*if(AsmUtil.imm12(bIR.sourceSecond.item)) //是imm12
                {*/
                    if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                        builder.add(reg, rn, bIR.sourceSecond.item);
                    }else{
                        builder.sub(reg, rn, bIR.sourceSecond.item);
                    }
                /*}else{
                    Reg tmp = regGetter.getTmpRegister();
                    dataHolder.addAndLoadFromFuncData(builder,bIR.sourceSecond.item,tmp);
                    if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                        builder.add(reg, rn, tmp);
                    }else{
                        builder.sub(reg, rn, tmp);
                    }
                }*/

            }else{
                Reg rn = regGetter.getReg(ir,bIR.sourceSecond);

                if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                    /*if(AsmUtil.imm12(bIR.sourceFirst.item))
                    {*/
                        builder.add(reg, rn, bIR.sourceFirst.item);
                    /*}else{
                        Reg tmp = regGetter.getTmpRegister();
                        dataHolder.loadFromFuncData(builder,bIR.sourceFirst.item,tmp);
                        builder.add(reg, rn, tmp);
                    }*/

                }else{
                    /*if(AsmUtil.imm8m(bIR.sourceFirst.item))
                    {*/
                        builder.regRegOperand(AsmBuilder.RegRegOperandOP.RSB,
                                              reg,
                                              rn,
                                              new ImmOperand(bIR.sourceFirst.item));
                    /*}else{
                        Reg tmp = regGetter.getTmpRegister();
                        dataHolder.loadFromFuncData(builder,bIR.sourceFirst.item,tmp);
                        builder.regRegOperand(AsmBuilder.RegRegOperandOP.RSB,
                                              reg,
                                              rn,
                                              new RegOperand(tmp));

                    }*/

                }
            }
        }else if(bIR.OP== BinocularRepre.Opcodes.DIV || bIR.OP== BinocularRepre.Opcodes.MOD)
        {
            //除法用__aeabi_idiv
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData ) //都不是立即数
            {
                Reg rd = regGetter.getReg(bIR, bIR.sourceFirst);
                Reg rn = regGetter.getReg(bIR, bIR.sourceSecond);
                builder.mov(Regs.R0,rd);
                builder.mov(Regs.R1,rn);
            }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                Reg rd = regGetter.getReg(bIR, bIR.sourceFirst);
                builder.mov(Regs.R0,rd);
                builder.mov(Regs.R1,bIR.sourceSecond.item);
            }else{

                Reg rn = regGetter.getReg(bIR, bIR.sourceSecond);
                builder.mov(Regs.R0,bIR.sourceFirst.item);
                builder.mov(Regs.R1,rn);
            }

            Reg target = regGetter.getReg(ir, bIR.target);
            List<Reg> usingRegister =
                    regGetter.getUsingRegNext().stream().filter(r->{
                        int id = r.getId();
                        return id>1 && target!=r;
                    }).sorted(Comparator.comparingInt(Reg::getId)).collect(Collectors.toList());

            AsmUtil.protectRegs(builder,regGetter,usingRegister);

            if(bIR.OP== BinocularRepre.Opcodes.DIV)
            {
                builder.bl("__aeabi_idiv");
                builder.mov(target,Regs.R0);
            }
            else{
                builder.bl("__aeabi_idivmod");
                builder.mov(target,Regs.R1);
            }

            AsmUtil.recoverRegs(builder,regGetter,usingRegister);
            //regGetter.setReg(ir, bIR.target, Regs.R0);
        }/*else if(bIR.OP== BinocularRepre.Opcodes.MOD)
        {
            //除法用__aeabi_idivmod
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                Reg rd = regGetter.getReg(bIR, bIR.sourceFirst);
                Reg rn = regGetter.getReg(bIR, bIR.sourceSecond);
                builder.mov(Regs.R0,rd);
                builder.mov(Regs.R1,rn);
            }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                Reg rd = regGetter.getReg(bIR, bIR.sourceFirst);
                builder.mov(Regs.R0,rd);
                builder.mov(Regs.R1,bIR.sourceSecond.item);
            }else{

                Reg rn = regGetter.getReg(bIR, bIR.sourceSecond);
                builder.mov(Regs.R0,bIR.sourceFirst.item);
                builder.mov(Regs.R1,rn);
            }

            Reg target = regGetter.getReg(ir, bIR.target);

            List<Reg> usingRegister =
                    regGetter.getUsingRegNext().stream().filter(r->{
                        int id = r.getId();
                        return id>1 && target!=r; //排除掉target，不然恢复的时候会把几所结果覆盖掉
                    }).sorted(Comparator.comparingInt(Reg::getId)).collect(Collectors.toList());

            AsmUtil.protectRegs(builder,regGetter,usingRegister);

            builder.bl("__aeabi_idivmod");
            //regGetter.setReg(ir, bIR.target, Regs.R1);

            builder.mov(target,Regs.R1);

            AsmUtil.recoverRegs(builder,regGetter,usingRegister);
        }*/
        else{


            Reg rd,rn;
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                rd = regGetter.getReg(bIR, bIR.sourceFirst);
                rn = regGetter.getReg(bIR, bIR.sourceSecond);
            }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                rn = regGetter.getTmpRegister();
                rd = regGetter.getReg(bIR, bIR.sourceFirst);
                builder.mov(rn, bIR.sourceSecond.item);
            }else{
                rd = regGetter.getTmpRegister();
                rn = regGetter.getReg(bIR, bIR.sourceSecond);
                builder.mov(rd,bIR.sourceFirst.item);
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
            }
            builder.tripleReg(op, reg, rd, rn);

        }

        return 1;
    }
}
