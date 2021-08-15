package compiler.optim;

import compiler.Util;
import compiler.asm.AddressRWInfo;
import compiler.genir.IRBlock;
import compiler.genir.IRCollection;
import compiler.genir.IRFunction;
import compiler.genir.code.*;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;
import java.util.stream.Collectors;

public class OptimizeProcessor {

    public static List<Optimizer> allOptimizer = new ArrayList<>();
    static {
        init();
    }
    public static void init()
    {

    }

    public static List<IRBlock> optimize(IRFunction irFunction)
    {
        jumpOptimize(irFunction);

        mulDivOptimize(irFunction);


        List<IRBlock> irBlocks = null;
        try {
            irBlocks = divideIntoBlock(irFunction);
        }catch (NullPointerException ne)
        {
            Util.printStackAndExit(-15,ne);
        }catch (Exception e)
        {
            Util.printStackAndExit(-16,e);
        }

        try {
            optimizeIrOrder(irBlocks);
        }catch (NullPointerException ne)
        {
            Util.printStackAndExit(-17,ne);
        }catch (Exception e)
        {
            Util.printStackAndExit(-18,e);
        }

        return irBlocks;
    }

    public static void jumpOptimize(IRFunction irFunction)
    {
        List<InterRepresent> flatIR = irFunction.getAllIR();
        for (int i = 0; i < flatIR.size(); i++) {
            InterRepresent ir = flatIR.get(i);
            if (!(ir instanceof GotoRepresent)) continue;

            GotoRepresent gotoIR = ((GotoRepresent) ir);
            GotoRepresent jumping = gotoIR;
            while (jumping.targetHolder.getInterRepresent() instanceof GotoRepresent
                    &&!(jumping.targetHolder.getInterRepresent() instanceof IfGotoRepresent)) {
                jumping = ((GotoRepresent) jumping.targetHolder.getInterRepresent());
            }

            gotoIR.targetHolder = jumping.targetHolder;

            if(i!=flatIR.size()-1)
            {
                if(gotoIR.targetHolder.getInterRepresent()== flatIR.get(i+1))
                {
                    irFunction.remove(gotoIR);
                }
            }

        }
    }

    public static void optimizeIrOrder(List<IRBlock> irBlocks)
    {
        for (IRBlock irBlock : irBlocks) {
            List<InterRepresent> irs = irBlock.getAllIR();
            HashSet<InterRepresent> irHasMoved=new HashSet<>();
            for (int i = 1; i < irs.size(); i++) {
                InterRepresent ir = irs.get(i);
                if(!(ir instanceof LoadRepresent))
                    continue;

                Collection<AddressRWInfo> allOutput = ir.getAllAddressRWInfo().stream().filter(
                        info->info.isWrite
                ).collect(Collectors.toList());
                if(allOutput.size()==0)
                    continue;

                int j = i+1;
                boolean skip = false;
                for(;j<irs.size();j++)
                {
                    InterRepresent irAfter = irs.get(j);
                    if(irAfter instanceof GotoRepresent)
                    {
                        skip = true;
                        break;
                    }
                    Collection<AddressRWInfo> allUse = irAfter.getAllAddressRWInfo().stream().filter(
                            info->!info.isWrite
                    ).collect(Collectors.toList());
                    if(allUse.size()==0)
                        continue;
                    if(allOutput.stream().anyMatch(allUse::contains))
                    {
                        break;
                    }
                }
                if(skip)
                    continue;
                if(j-i>1 && !irHasMoved.contains(ir))
                {
                    irHasMoved.add(ir);
                    irBlock.moveDown(i, j-1);
                    i--;
                }
            }
        }
    }
    /**
     * 将中间代码划分为块
     */
    public static List<IRBlock> divideIntoBlock(IRFunction irFunction) {
        List<IRBlock> result = new ArrayList<>();

        FuncSymbol funcSymbol = irFunction.funcSymbol;
        List<InterRepresent> codes = irFunction.getAllIR();
        Set<InterRepresent> enterPoints = new HashSet<>();
        int len = codes.size();
        // 第一条语句
        enterPoints.add(codes.get(0));
        int labelID = 0;

        try {
            for (int i = 1; i < len; i++) {
                InterRepresent ir = codes.get(i);
                if (ir instanceof GotoRepresent) {
                    // goto语句的下一条语句
                    enterPoints.add(codes.get(i + 1));

                    GotoRepresent gotoIR = (GotoRepresent) ir;
                    // 目标语句
                    enterPoints.add((gotoIR).getTargetIR());

                    if(gotoIR instanceof IfGotoRepresent)
                    {
                        if(gotoIR.targetHolder==null)
                            System.exit(161);
                        if(gotoIR.getTargetIR()==null)
                            System.exit(30+gotoIR.flag);
                    }else{
                        String description = irFunction.getDescription(i);
                        if(gotoIR.targetHolder==null)
                        {
                            if(description.equals("break"))
                                System.exit(171);
                            if(description.equals("continue"))
                                System.exit(173);
                            if(description.equals("while end"))
                                System.exit(175);

                            System.exit(177);
                        }

                        if(gotoIR.getTargetIR()==null)
                        {
                            if(description.equals("break"))
                                System.exit(172);
                            if(description.equals("continue"))
                                System.exit(174);
                            if(description.equals("while end"))
                                System.exit(176);

                            System.exit(178);
                        }
                    }


                    String label;
                /*if (ir.hasLabel()) label = ir.getLabel();
                else {*/
                    label = String.format("%s.%d", funcSymbol.getFuncName(), labelID++);
                    //}
                    (gotoIR).getTargetIR().setLabel(label);
                }
            }
        }catch (NullPointerException e)
        {
            Util.printStackAndExit(157, e);
        }

        try {
            for (int i = 0; i < codes.size(); i++) {
                InterRepresent ir = codes.get(i);

                if (enterPoints.contains(ir)) {



                    IRBlock block = new IRBlock(ir.getLabel());
                    block.addIR(ir);
                    while (++i < len && !enterPoints.contains(codes.get(i))) {
                        // TODO 还未考虑halt
                        block.addIR(codes.get(i));
                    }
                    i--;

                    result.add(block);
                }
            }
        }catch (NullPointerException e)
        {
            Util.printStackAndExit(158,e);
        }
        return result;
    }

    private static void mulDivOptimize(IRCollection irCollection)
    {
        List<InterRepresent> irs = new ArrayList<>(irCollection.getAllIR());
        for (InterRepresent ir : irs) {
            if(ir instanceof BinocularRepre)
            {
                BinocularRepre bIR = ((BinocularRepre) ir);
                if(bIR.OP== BinocularRepre.Opcodes.MUL || bIR.OP== BinocularRepre.Opcodes.DIV)
                {
                    if(bIR.OP== BinocularRepre.Opcodes.MUL && bIR.sourceFirst.isData && bIR.sourceFirst.item==1)
                    {
                        irCollection.getAllIR().stream().map(InterRepresent::getAllAddressRWInfo).forEach(
                                ads-> ads.forEach(adInfo->{
                                    if (adInfo.address.equals(bIR.target))
                                    {
                                        adInfo.address.item = bIR.sourceSecond.item;
                                    }
                                })
                        );
                        irCollection.remove(bIR);
                    }else if(bIR.sourceSecond.isData && bIR.sourceSecond.item==1)
                    {
                        irCollection.getAllIR().stream().map(InterRepresent::getAllAddressRWInfo).forEach(
                                ads-> ads.forEach(adInfo->{
                                    if (adInfo.address.equals(bIR.target))
                                    {
                                        adInfo.address.item = bIR.sourceFirst.item;
                                    }
                                })
                        );
                        irCollection.remove(bIR);
                    }
                }
            }
        }
    }
}
