package compiler.asm.converter;

import compiler.ConstDef;
import compiler.LazyGetter;
import compiler.asm.*;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.LSRepresent;
import compiler.genir.code.LoadRepresent;
import compiler.symboltable.*;
import compiler.symboltable.function.FuncSymbol;

import java.util.Collection;
import java.util.List;

public class LoadConverter extends LSConverter {
    @Override
    public boolean needProcess(InterRepresent ir, Collection<InterRepresent> allIR, int index) {
        return ir instanceof LoadRepresent;
    }

    @Override
    public int process(AsmBuilder builder, RegGetter regGetter, InterRepresent ir, List<InterRepresent> allIR, int index, FuncSymbol funcSymbol, FunctionDataHolder dataHolder) {
        LoadRepresent loadIr = (LoadRepresent) ir;
        ValueSymbol valueSymbol = loadIr.valueSymbol;

        RegGetter.LoadSaveInfo loadSaveInfo = regGetter.getLoadSaveInfo(funcSymbol);
        if(ConstDef.avoidUselessLoad && loadSaveInfo.getCanLastLoadedUse(valueSymbol, loadIr.offset)) //之前已读取，且没有被修改
            // (由SaveConverter设置)
        {
            AddressOrData addressOrData = loadSaveInfo.getLastLoadAddress(valueSymbol,loadIr.offset);
            RegGetter.RegOrMem regOrMem = regGetter.getReg(addressOrData);
            if(regOrMem.inMem)
            {
                Reg targetReg = regGetter.distributeReg(loadIr,loadIr.target);
                regGetter.loadStagedFromMem(regOrMem, targetReg);
                loadSaveInfo.putLastLoadAddress(valueSymbol,loadIr.offset,loadIr.target);
                return 1;
            }else {
                AddressRWInfo addressInReg = regGetter.getAddressInReg(regOrMem.reg);
                if(addressInReg!=null && addressInReg.address.equals(addressOrData)) // 寄存器里依然保存着上次读取的值
                {
                    //regGetter.setReg(loadIr,loadIr.target,regOrMem.reg);
                    Reg reg = regGetter.distributeReg(ir, loadIr.target, r -> r != regOrMem.reg);
                    builder.mov(reg,regOrMem.reg);
                    loadSaveInfo.putCanLastLoadedUse(valueSymbol,loadIr.offset,true);
                    loadSaveInfo.putLastLoadAddress(valueSymbol,loadIr.offset,loadIr.target);
                    return 1;
                }
            }
        }

        loadSaveInfo.putCanLastLoadedUse(valueSymbol,loadIr.offset,true);
        loadSaveInfo.putLastLoadAddress(valueSymbol,loadIr.offset,loadIr.target);

        boolean flag = false;
        if (valueSymbol instanceof ConstSymbol) {
            ConstSymbol symbol = (ConstSymbol) valueSymbol;
            if(loadIr.offset==null)
            {
                builder.mov(regGetter.distributeReg(ir, loadIr.target), symbol.initValues.get(0));
                flag = true;
            }else if(loadIr.offset.isData){
                builder.mov(regGetter.distributeReg(ir, loadIr.target), symbol.initValues.get(loadIr.offset.item));
                flag = true;
            }
        }
        if(!flag)
            return super.process(AsmBuilder.Mem.LDR, builder, regGetter, (LSRepresent) ir,funcSymbol, dataHolder,
                                 new LazyGetter<>(()->regGetter.distributeReg(ir, ((LSRepresent) ir).target)));
        else
            return 1;
    }

}
