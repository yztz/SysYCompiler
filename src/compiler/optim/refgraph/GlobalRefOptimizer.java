package compiler.optim.refgraph;

import compiler.asm.AddressRWInfo;
import compiler.genir.IRBlock;
import compiler.genir.IRCollection;
import compiler.genir.IRFunction;
import compiler.genir.IRUnion;
import compiler.genir.code.*;
import compiler.optim.IRBlockDivider;
import compiler.symboltable.*;
import compiler.symboltable.function.AbstractFuncSymbol;
import compiler.symboltable.function.ExternalFuncSymbol;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalRefOptimizer {
    IRUnion irUnion;
    FuncSymbolTable funcSymbolTable;
    SymbolTableHost symbolTableHost;
    private Map<InterRepresent, FuncSymbol> irBelongFunc = new HashMap<>();
    private Map<AbstractFuncSymbol,List<CallRepresent>> funcCaller = new HashMap<>();
    private Map<FuncSymbol,IRFunction> functionMap = new HashMap<>();
    private Map<FuncSymbol,IRBlockDivider> dividerMap = new HashMap<>();
    IRFunction mainFunction = null;
    public GlobalRefOptimizer(IRUnion irUnion, FuncSymbolTable funcSymbolTable, SymbolTableHost symbolTableHost) {
        this.irUnion = irUnion;
        this.funcSymbolTable = funcSymbolTable;
        this.symbolTableHost = symbolTableHost;
    }

    public void optimize()
    {
        List<IrRefNode> outputNode = new ArrayList<>();


        IRCollection allIR = new IRCollection();

        //有用的IR，没用的将会被删除
        HashSet<InterRepresent> usedIr = new HashSet<>();

        for (IRCollection irCollection : irUnion.getAll()) {
            boolean isMainFunction =
                    irCollection instanceof IRFunction && ((IRFunction) irCollection).funcSymbol.getFuncName().equals("main");
            if(isMainFunction)
                mainFunction = (IRFunction) irCollection;

            allIR.addCodes(irCollection);

            if(irCollection instanceof IRFunction)
            {
                FuncSymbol funcSymbol = ((IRFunction) irCollection).funcSymbol;
                functionMap.put(funcSymbol,(IRFunction) irCollection);
                dividerMap.put(funcSymbol,new IRBlockDivider(((IRFunction) irCollection)));
            }
            for (InterRepresent ir : irCollection.getAllIR()) {

                if(irCollection instanceof IRFunction)
                    irBelongFunc.put(ir,((IRFunction) irCollection).funcSymbol);
                if(ir instanceof ReturnRepresent && ((ReturnRepresent) ir).defaultReturn)
                {
                    usedIr.add(ir);
                }else if(ir instanceof CallRepresent)
                {
                    CallRepresent caller = (CallRepresent) ir;
                    AbstractFuncSymbol funcSymbol = (caller).funcSymbol;
                    if(funcSymbol instanceof ExternalFuncSymbol)
                        outputNode.add(new IrRefNode(ir)); //外部函数，添加到输出节点

                    if(!funcCaller.containsKey(funcSymbol))
                        funcCaller.put(funcSymbol,new ArrayList<>());

                    List<CallRepresent> callers = funcCaller.get(funcSymbol);
                    callers.add(caller);

                }else if(isMainFunction && ir instanceof ReturnRepresent)
                    outputNode.add(new IrRefNode(ir));
            }
        }



        System.out.println("All output node");
        HashSet<RefNode> passed = new HashSet<>();
        for (IrRefNode refNode : outputNode) {
            System.out.printf("%n refs of %s :%n",refNode.ir.toString());
            buildNodeInternal(passed,allIR,refNode);
            usedIr.add(refNode.ir);
        }

        for (RefNode refNode : passed) {
            if(refNode instanceof IrRefNode)
                usedIr.add(((IrRefNode) refNode).ir);
        }

        System.out.printf("%n used ir :%n");
        for (IRCollection irCollection : irUnion.getAll()) {
            System.out.println();
            for (InterRepresent ir : irCollection.getAllIR()) {
                System.out.printf("%s %s%n",!usedIr.contains(ir)?"//":"  ",ir);
            }
        }
        for (IRCollection irs : irUnion.getAll()) {

            // 删除无用的代码前，调整goto目标
            for (InterRepresent ir : irs.getAllIR()) {
                if(ir instanceof GotoRepresent)
                {
                    GotoRepresent gotoIr = ((GotoRepresent) ir);
                    if(usedIr.contains(gotoIr.getTargetIR()))
                        continue;

                    int index = irs.getAllIR().indexOf(gotoIr.getTargetIR());
                    boolean resetTarget=false;
                    for(int i = index+1;i<irs.getAllIR().size();i++)
                    {
                        InterRepresent checking = irs.getAllIR().get(i);
                        if(usedIr.contains(checking) || checking instanceof GotoRepresent)
                        {
                            gotoIr.targetHolder.setInterRepresent(checking);
                            resetTarget=true;
                            break;
                        }
                    }
                }
            }

            irs.getAllIR().removeIf(ir->!usedIr.contains(ir));
        }
        irUnion.getAll().removeIf(irs->irs.getLineOccupied()==0);
    }
    private void buildNodeInternal(Set<RefNode> passed,IRCollection irCollection,RefNode current)
    {
        Set<RefNode> refs = null;
        if(current instanceof IrRefNode)
        {
            IrRefNode irCurrent = (IrRefNode) current;
            refs = findRef(irCollection, irCurrent.ir);
           if(irCurrent.ir instanceof LoadRepresent)
           {
               ValueSymbol valueSymbol = ((LoadRepresent) irCurrent.ir).valueSymbol;
               VarRefNode varRefNode=new VarRefNode(valueSymbol);
               refs.add(varRefNode);
           }
            if(irCurrent.ir instanceof LAddrRepresent)
            {
                ValueSymbol valueSymbol = ((LAddrRepresent) irCurrent.ir).valueSymbol;
                VarRefNode varRefNode=new VarRefNode(valueSymbol);
                refs.add(varRefNode);
            }

            // 所有跳转到当前IR所属block的goto或者ifGoto语句
            IRBlock irBlock = getIrBelongBlock(irCurrent.ir);
            refs.addAll(irBlock.jumpFrom.stream().map(IrRefNode::new).collect(Collectors.toSet()));

            //所有调用当前IR所属函数的CallRepresent
            FuncSymbol funcSymbol = irBelongFunc.get(irCurrent.ir);
            if (funcCaller.containsKey(funcSymbol)) {
                refs.addAll(funcCaller.get(funcSymbol).stream().map(IrRefNode::new).collect(Collectors.toSet()));
            }
        }else if(current instanceof VarRefNode)
        {
            VarRefNode varCurrent = (VarRefNode) current;
            refs = findRef(irCollection,varCurrent.valueSymbol);
        }


        if(refs==null || refs.size()==0) return;


        current.refs.addAll(refs);
        for (RefNode ref : current.refs) {
            if(passed.contains(ref)) continue;
            passed.add(ref);
            System.out.println(ref);
            buildNodeInternal(passed,irCollection,ref);
        }
    }
    private Set<RefNode> findRef(IRCollection irCollection, InterRepresent ir)
    {
        Set<AddressOrData> refAddress =
                ir.getAllAddressRWInfo().stream().filter(info->!info.isWrite).map(info->info.address)
                .collect(Collectors.toSet());
        List<InterRepresent> allIR = irCollection.getAllIR();
        int i = allIR.indexOf(ir)-1;

        HashSet<InterRepresent> refs = new HashSet<>();
        for (; i >=0 ; i--) {
            InterRepresent checkIr = allIR.get(i);
            for (AddressRWInfo info : checkIr.getAllAddressRWInfo()) {
                if(info.isWrite && refAddress.contains(info.address)){
                    if(checkIr instanceof CallRepresent &&
                        ((CallRepresent) checkIr).funcSymbol instanceof FuncSymbol)
                    {
                        //函数调用需做特殊处理，内部函数，返回值才是依赖的目标
                        CallRepresent callIr = (CallRepresent) checkIr;
                        refs.addAll(getAllReturnIR(((FuncSymbol) callIr.funcSymbol)));
                    }else{
                        refs.add(checkIr);
                    }

                }
            }
        }

        return refs.stream().map(IrRefNode::new).collect(Collectors.toSet());
    }

    private Set<ReturnRepresent> getAllReturnIR(FuncSymbol funcSymbol)
    {
        Set<ReturnRepresent> result = new HashSet<>();
        IRFunction irFunction = functionMap.get(funcSymbol);
        for (InterRepresent ir : irFunction.getAllIR()) {
            if(ir instanceof ReturnRepresent)
                result.add((ReturnRepresent)ir);
        }

        return result;
    }

    private Set<RefNode> findRef(IRCollection irCollection, ValueSymbol valueSymbol)
    {
        HashSet<InterRepresent> refs = new HashSet<>();
        if(valueSymbol instanceof VarSymbol && ((VarSymbol) valueSymbol).isGlobal)
        {
            deepSearchSymbolWriting(refs,mainFunction,valueSymbol);
        }
        for (InterRepresent ir : irCollection.getAllIR()) {
            if(ir instanceof SaveRepresent){
                SaveRepresent saveIR = ((SaveRepresent) ir);
                if (saveIR.valueSymbol==valueSymbol) {
                    refs.add(saveIR);
                }
            }else if(ir instanceof InitVarRepresent)
            {
                InitVarRepresent saveIR = ((InitVarRepresent) ir);
                if (saveIR.varSymbol==valueSymbol) {
                    refs.add(saveIR);
                }
            }else if(ir instanceof LAddrRepresent)
            {
                LAddrRepresent lAddrIr  = ((LAddrRepresent) ir);
                ValueSymbol symbol = lAddrIr.valueSymbol;
                FuncSymbol funcSymbol = irBelongFunc.get(ir);
                deepSearchSymbolWriting(refs,functionMap.get(funcSymbol),symbol);
            }
        }

        return refs.stream().map(IrRefNode::new).collect(Collectors.toSet());
    }
    Map<ValueSymbol,Map<FuncSymbol,Set<InterRepresent>>> searchCache = new HashMap<>();
    private void deepSearchSymbolWriting(Set<InterRepresent> saveIRs,
                                         IRFunction irFunc,
                                         ValueSymbol symbol)
    {

        // 避免重复搜索
        if(searchCache.containsKey(symbol) && searchCache.get(symbol).containsKey(irFunc.funcSymbol))
        {
            saveIRs.addAll(searchCache.get(symbol).get(irFunc.funcSymbol));
            return;
        }

        if (!searchCache.containsKey(symbol)) {
            searchCache.put(symbol, new HashMap<>());
        }
        searchCache.get(symbol).put(irFunc.funcSymbol, new HashSet<>());

        AddressOrData target = null;

        for (int i = 0; i < irFunc.getAllIR().size(); i++) {
            InterRepresent ir = irFunc.getAllIR().get(i);
            if(ir instanceof SaveRepresent)
            {
                SaveRepresent saveIR = (SaveRepresent) ir;
                if(saveIR.valueSymbol==symbol)
                {
                    saveIRs.add(saveIR);
                    searchCache.get(symbol).get(irFunc.funcSymbol).add(saveIR);
                }
            }else if(ir instanceof LAddrRepresent){
                if(((LAddrRepresent) ir).valueSymbol==symbol)
                    target = ((LAddrRepresent) ir).target;
            }
            else if(ir instanceof CallRepresent)
            {
                if(target==null) continue;
                CallRepresent callIR = ((CallRepresent) ir);
                if(callIR.params==null || !(callIR.funcSymbol instanceof FuncSymbol)) continue;
                for (int j = 0; j < callIR.params.length; j++) {
                    AddressOrData param = callIR.params[j];
                    if(param.equals(target)) // 这个caller使用了这个地址
                    {
                        FuncSymbol funcSymbol = (FuncSymbol) callIR.funcSymbol;
                        ParamSymbol paramSymbol = funcSymbol.paramSymbols.get(j); // 地址被赋给了这个参数

                        IRFunction functionIRs = functionMap.get(funcSymbol);
                        deepSearchSymbolWriting(saveIRs,functionIRs,paramSymbol);
                    }
                }
            }
        }
    }

    private IRBlock getIrBelongBlock(InterRepresent ir)
    {
        FuncSymbol funcSymbol = irBelongFunc.get(ir);
        IRBlockDivider divider = dividerMap.get(funcSymbol);
        return divider.getIrBelongBlock(ir);
    }
}
