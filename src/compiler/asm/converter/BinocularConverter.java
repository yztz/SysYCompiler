package compiler.asm.converter;

import compiler.ConstDef;
import compiler.Util;
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
            Reg reg = regGetter.distributeReg(ir, bIR.target);

            //AsmUtil.dealIfNotImm12(result,reg,builder,dataHolder);

            builder.mov(reg,result);
            return 1;
        }

        if(bIR.OP == BinocularRepre.Opcodes.MUL)
        {
            if(bIR.sourceFirst.isData && !bIR.sourceSecond.isData)
            {

                int num = bIR.sourceFirst.item;
                if(Util.isPow2(num))
                {
                    Reg reg = regGetter.distributeReg(bIR,bIR.target);
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceSecond);
                    int pow2 = Util.getPow2(num);
                    builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.LSL,reg,rd,pow2);
                    return 1;
                }
            }else if(!bIR.sourceFirst.isData && bIR.sourceSecond.isData)
            {
                int num = bIR.sourceSecond.item;
                if(Util.isPow2(num))
                {
                    Reg reg = regGetter.distributeReg(bIR,bIR.target);
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceFirst);
                    int pow2 = Util.getPow2(num);
                    builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.LSL,reg,rd,pow2);
                    return 1;
                }
            }
        }else if(bIR.OP == BinocularRepre.Opcodes.DIV)
        {
            if(!bIR.sourceFirst.isData && bIR.sourceSecond.isData)
            {
                int num = bIR.sourceSecond.item;
                if(Util.isPow2(num))
                {
                    Reg reg = regGetter.distributeReg(bIR,bIR.target);
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceFirst);
                    int pow2 = Util.getPow2(num);
                    builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.ASR,reg,rd,pow2);
                    return 1;
                }
            }
        }else if(bIR.OP == BinocularRepre.Opcodes.MOD && ConstDef.modPow2Optimize)
        {
            if(!bIR.sourceFirst.isData && bIR.sourceSecond.isData)
            {
                int num = bIR.sourceSecond.item;
                if(Util.isPow2(num))
                {
                    Reg reg = regGetter.distributeReg(bIR,bIR.target);
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceFirst); //左操作数
                    int pow2 = Util.getPow2(num);
                    builder.regRegOperand(AsmBuilder.RegRegOperandOP.AND,reg,rd,new ImmOperand(pow2));
                }
                /*if(Util.isPow2(num))
                {
                    Reg reg = regGetter.distributeReg(bIR,bIR.target);
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceFirst); //左操作数
                    int pow2 = Util.getPow2(num);

                    if(reg!=rd)
                    {
                        builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.ASR,reg,rd,pow2);
                        builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.LSL,reg,reg,pow2);
                        builder.sub(reg,rd,reg);
                    }else{
                        //如果reg和rd是同一个
                        Reg tmp = regGetter.getTmpRegister();
                        builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.ASR,tmp,rd,pow2);
                        builder.regRegImmOrReg(AsmBuilder.RegRegImmOrRegOP.LSL,tmp,tmp,pow2);
                        builder.sub(reg,rd,tmp);
                        regGetter.releaseReg(tmp);
                    }
                    return 1;
                }*/
            }
        }


        Reg reg = regGetter.distributeReg(ir, bIR.target);

        if(bIR.OP== BinocularRepre.Opcodes.ADD || bIR.OP== BinocularRepre.Opcodes.MINUS)
        {
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                AsmBuilder.RegRegOperandOP op = bIR.OP == BinocularRepre.Opcodes.ADD ? AsmBuilder.RegRegOperandOP.ADD : AsmBuilder.RegRegOperandOP.SUB;

                builder.regRegOperand(op, reg, regGetter.distributeReg(ir, bIR.sourceFirst),
                                      new RegOperand(regGetter.distributeReg(ir, bIR.sourceSecond)));
            }else if(!bIR.sourceFirst.isData){
                Reg rn = regGetter.distributeReg(ir, bIR.sourceFirst);
                int imm = bIR.sourceSecond.item;
                if(imm>=0)
                {
                    if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                        builder.add(reg, rn, imm);
                    }else{
                        builder.sub(reg, rn, imm);
                    }
                }else{
                    if (bIR.OP== BinocularRepre.Opcodes.ADD) {
                        builder.sub(reg, rn, -imm);
                    }else{
                        builder.add(reg, rn, -imm);
                    }
                }


            }else{
                Reg rn = regGetter.distributeReg(ir, bIR.sourceSecond);

                if (bIR.OP== BinocularRepre.Opcodes.ADD) {

                        builder.add(reg, rn, bIR.sourceFirst.item);

                }else{
                        builder.regRegOperand(AsmBuilder.RegRegOperandOP.RSB,
                                              reg,
                                              rn,
                                              new ImmOperand(bIR.sourceFirst.item));
                }
            }
        }else if(bIR.OP== BinocularRepre.Opcodes.DIV || bIR.OP== BinocularRepre.Opcodes.MOD)
        {


            if(ConstDef.armv7ve)
            {
                Reg rl;
                Reg rr;
                if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData ) //都不是立即数
                {
                    rl = regGetter.distributeReg(bIR, bIR.sourceFirst);
                    rr = regGetter.distributeReg(bIR, bIR.sourceSecond);
                }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                    rl = regGetter.distributeReg(bIR, bIR.sourceFirst);
                    rr = regGetter.getTmpRegister();
                    builder.mov(rr,bIR.sourceSecond.item);
                }else{

                    rr = regGetter.distributeReg(bIR, bIR.sourceSecond);
                    rl = regGetter.getTmpRegister();
                    builder.mov(rl,bIR.sourceFirst.item);
                }

                Reg target = regGetter.distributeReg(ir, bIR.target);
                if(bIR.OP== BinocularRepre.Opcodes.DIV)
                {
                    builder.sdiv(target,rl,rr);
                }
                else{
                    if(rl!=target)
                    {
                        builder.sdiv(target,rl,rr);
                        builder.mul(target,target,rr);
                        builder.sub(target,rl,target);
                    }else{
                        Reg tmp = regGetter.getTmpRegister();
                        builder.sdiv(tmp,rl,rr);
                        builder.mul(tmp,tmp,rr);
                        builder.sub(target,rl,tmp);
                        regGetter.releaseReg(tmp);
                    }
                }
            }else{
                //除法用__aeabi_idiv
                if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData ) //都不是立即数
                {
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceFirst);
                    Reg rn = regGetter.distributeReg(bIR, bIR.sourceSecond);
                    builder.mov(Regs.R0,rd);
                    builder.mov(Regs.R1,rn);
                }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                    Reg rd = regGetter.distributeReg(bIR, bIR.sourceFirst);
                    builder.mov(Regs.R0,rd);
                    builder.mov(Regs.R1,bIR.sourceSecond.item);
                }else{

                    Reg rn = regGetter.distributeReg(bIR, bIR.sourceSecond);
                    builder.mov(Regs.R0,bIR.sourceFirst.item);
                    builder.mov(Regs.R1,rn);
                }

                Reg target = regGetter.distributeReg(ir, bIR.target);

                List<Reg> usingRegister =
                        regGetter.getUsingRegNext().stream().filter(r->{
                            int id = r.getId();
                            return id>1 && target!=r;
                        }).sorted(Comparator.comparingInt(Reg::getId)).collect(Collectors.toList());

                AsmUtil.protectRegs(builder,regGetter,usingRegister,funcSymbol);

                if(bIR.OP== BinocularRepre.Opcodes.DIV)
                {
                    builder.bl("__aeabi_idiv");
                    builder.mov(target,Regs.R0);
                }
                else{
                    builder.bl("__aeabi_idivmod");
                    builder.mov(target,Regs.R1);
                }

                AsmUtil.recoverRegs(builder,regGetter,usingRegister,funcSymbol);
            }
        }
        else{


            Reg rd,rn;
            if(!bIR.sourceFirst.isData && !bIR.sourceSecond.isData) //都不是立即数
            {
                rd = regGetter.distributeReg(bIR, bIR.sourceFirst);
                rn = regGetter.distributeReg(bIR, bIR.sourceSecond);
            }else if(!bIR.sourceFirst.isData) { //右边的是立即数
                rn = regGetter.getTmpRegister();
                rd = regGetter.distributeReg(bIR, bIR.sourceFirst);
                builder.mov(rn, bIR.sourceSecond.item);
            }else{

                rd = regGetter.getTmpRegister();
                rn = regGetter.distributeReg(bIR, bIR.sourceSecond);
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
