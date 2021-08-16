package compiler.optim;

import compiler.ConstDef;
import compiler.Util;
import compiler.asm.AddressRWInfo;
import compiler.genir.IRBlock;
import compiler.genir.IRCollection;
import compiler.genir.IRFunction;
import compiler.genir.IRUnion;
import compiler.genir.code.*;
import compiler.optim.refgraph.GlobalRefOptimizer;
import compiler.symboltable.FuncSymbolTable;
import compiler.symboltable.SymbolTableHost;

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
    public static void globalOptimize(IRUnion irUnion, FuncSymbolTable funcSymbolTable, SymbolTableHost symbolTableHost)
    {
        if(ConstDef.globalOptimize)
        {
            GlobalRefOptimizer globalRefOptimizer =new GlobalRefOptimizer(irUnion, funcSymbolTable, symbolTableHost);
            globalRefOptimizer.optimize();
        }
    }
    public static List<IRBlock> optimize(IRFunction irFunction)
    {
        jumpOptimize(irFunction);

        if(ConstDef.removeUselessMulDiv)
            mulDivOptimize(irFunction);


        List<IRBlock> irBlocks = null;
        try {
            IRBlockDivider divider=new IRBlockDivider(irFunction);
            irBlocks = divider.getBlocks();
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

    // 去掉乘1除1等操作
    private static void mulDivOptimize(IRCollection irCollection)
    {
        List<InterRepresent> irs = new ArrayList<>(irCollection.getAllIR());
        for (InterRepresent ir : irs) {
            if(ir instanceof BinocularRepre)
            {
                BinocularRepre bIR = ((BinocularRepre) ir);
                if(bIR.OP== BinocularRepre.Opcodes.MUL || bIR.OP== BinocularRepre.Opcodes.DIV)
                {
                    if(bIR.sourceSecond.isData && bIR.sourceSecond.item==1)
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
                    }else if(bIR.OP== BinocularRepre.Opcodes.MUL)
                    {
                        if(bIR.sourceFirst.isData && bIR.sourceFirst.item==1)
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
                        }
                    }
                }
            }
        }
    }
}
