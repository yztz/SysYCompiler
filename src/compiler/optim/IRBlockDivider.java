package compiler.optim;

import compiler.Util;
import compiler.genir.IRBlock;
import compiler.genir.IRFunction;
import compiler.genir.code.GotoRepresent;
import compiler.genir.code.IfGotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.symboltable.function.FuncSymbol;
import org.antlr.v4.runtime.misc.MultiMap;

import java.util.*;

public class IRBlockDivider {
    IRFunction irFunction;
    List<IRBlock> irBlocks;
    private Map<InterRepresent,IRBlock> irBelongBlock=new HashMap<>();
    public IRBlockDivider(IRFunction irFunction) {
        this.irFunction = irFunction;
    }
    public List<IRBlock> getBlocks()
    {
        if(irBlocks==null)
            irBlocks = divideIntoBlock(irFunction);

        return irBlocks;
    }

    public IRBlock getIrBelongBlock(InterRepresent ir)
    {
        if(irBlocks==null)
            irBlocks = divideIntoBlock(irFunction);
        return irBelongBlock.get(ir);
    }

    /**
     * 将中间代码划分为块
     */
    private List<IRBlock> divideIntoBlock(IRFunction irFunction) {
        List<IRBlock> result = new ArrayList<>();

        FuncSymbol funcSymbol = irFunction.funcSymbol;
        List<InterRepresent> codes = irFunction.getAllIR();
        Set<InterRepresent> enterPoints = new HashSet<>();
        MultiMap<InterRepresent,GotoRepresent> jumpFrom = new MultiMap<>();
        int len = codes.size();
        // 第一条语句
        enterPoints.add(codes.get(0));
        int labelID = 0;

        try {
            for (int i = 1; i < len; i++) {
                InterRepresent ir = codes.get(i);
                ir.setLabel(null);

                if (ir instanceof GotoRepresent) {
                    GotoRepresent gotoIR = (GotoRepresent) ir;

                    InterRepresent nextIR = codes.get(i + 1);
                    // goto语句的下一条语句
                    enterPoints.add(nextIR);
                    if(!jumpFrom.containsKey(nextIR))
                        jumpFrom.put(nextIR,new ArrayList<>());
                    jumpFrom.get(nextIR).add(gotoIR);

                    // 目标语句
                    InterRepresent targetIR = gotoIR.getTargetIR();
                    enterPoints.add(targetIR);
                    if(!jumpFrom.containsKey(targetIR))
                        jumpFrom.put(targetIR, new ArrayList<>());
                    jumpFrom.get(targetIR).add(gotoIR);

                    if(gotoIR instanceof IfGotoRepresent)
                    {
                        if(gotoIR.targetHolder==null)
                            System.exit(161);
                        if(targetIR ==null)
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

                        if(targetIR ==null)
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

                    String label;
                    label = String.format("%s.%d", funcSymbol.getFuncName(), labelID++);
                    ir.setLabel(label);

                    irBelongBlock.put(ir,block);
                    if (jumpFrom.containsKey(ir)) {
                        block.jumpFrom = jumpFrom.get(ir);
                    }


                    while (++i < len && !enterPoints.contains(codes.get(i))) {
                        // TODO 还未考虑halt
                        block.addIR(codes.get(i));
                        irBelongBlock.put(codes.get(i),block);
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
}
