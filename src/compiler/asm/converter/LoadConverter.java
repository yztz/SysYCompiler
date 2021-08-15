package compiler.asm.converter;

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
        if(canLastLoadUse.containsKey(valueSymbol) &&
                canLastLoadUse.get(valueSymbol)) //之前已读取，且没有被修改(由SaveConverter设置)
        {
            AddressOrData addressOrData = lastLoadAddress.get(valueSymbol);
            RegGetter.RegOrMem regOrMem = regGetter.getReg(addressOrData);
            if(regOrMem.inMem)
            {
                Reg targetReg = regGetter.distributeReg(loadIr,loadIr.target);
                regGetter.loadStagedFromMem(regOrMem, targetReg);
                lastLoadAddress.put(valueSymbol,loadIr.target);
                return 1;
            }else {
                AddressRWInfo addressInReg = regGetter.getAddressInReg(regOrMem.reg);
                if(addressInReg!=null && addressInReg.address.equals(addressOrData)) // 寄存器里依然保存着上次读取的值
                {
                    regGetter.setReg(loadIr,loadIr.target,regOrMem.reg);
                    lastLoadAddress.put(valueSymbol,loadIr.target);
                    return 1;
                }
            }
        }

        canLastLoadUse.put(valueSymbol,true);
        lastLoadAddress.put(valueSymbol,loadIr.target);

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
                                 ()->regGetter.distributeReg(ir, ((LSRepresent) ir).target));
        else
            return 1;
    }

}
