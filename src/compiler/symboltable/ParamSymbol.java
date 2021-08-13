package compiler.symboltable;

import compiler.ConstDef;
import compiler.asm.AddressRWInfo;
import compiler.genir.IRCollection;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.WrittenRepresent;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamSymbol extends ValueSymbol{
    public int offsetByte = 0;
    private IRCollection irToCalDimSize;
    private AddressOrData[] dimensions;
    public ParamSymbol(Token symbolToken) {
        super(symbolToken);
    }

    public ParamSymbol(Token symbolToken, AddressOrData[] dimensions, boolean isArray) {
        super(symbolToken,isArray);
        array = true;
        this.dimensions = dimensions;
    }

    public void setIrToCalDimSize(IRCollection irToCalDimSize,AddressOrData[] dimResult) {
        this.irToCalDimSize = irToCalDimSize;
        this.dimensions = dimResult;
    }
    public int getDimLength()
    {
        return dimensions.length;
    }
    public IrCalDim getDimCalculator()
    {
        List<InterRepresent> copyIrs = new ArrayList<>();
        List<InterRepresent> allIR = irToCalDimSize.getAllIR();
        Map<AddressOrData,AddressOrData> replacedAddress = new HashMap<>();
        for (InterRepresent ir : allIR) {
            if(ir instanceof WrittenRepresent)
            {
                AddressOrData oldAddress = ((WrittenRepresent) ir).target;
                if(!replacedAddress.containsKey(oldAddress))
                {
                    replacedAddress.put(oldAddress,AddressOrData.createNewAddress());
                }
            }
            InterRepresent copyIR = ir.createCopy();
            for (AddressRWInfo rwInfo : copyIR.getAllAddressRWInfo()) {
                if(replacedAddress.containsKey(rwInfo.address))
                {
                    AddressOrData ad = replacedAddress.get(rwInfo.address);
                    rwInfo.address.item = ad.item;
                    rwInfo.address.isData = ad.isData;
                }
            }
            copyIrs.add(copyIR);
        }

        AddressOrData[] newDimResult = new AddressOrData[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            if(replacedAddress.containsKey(dimensions[i]))
            {
                newDimResult[i]= replacedAddress.get(dimensions[i]);
            }else{
                newDimResult[i]= dimensions[i];
            }

        }


        return new IrCalDim(newDimResult, copyIrs);
    }

    private boolean array;
    @Override
    public boolean isArray() {
        return array;
    }

    @Override
    public int getOffsetByte() {
        return offsetByte;
    }


    public int getByteSize() {
        return ConstDef.WORD_SIZE;
    }

    public static class IrCalDim extends IRCollection{
        public AddressOrData[] dimensions;

        public IrCalDim(AddressOrData[] dimensions, List<InterRepresent> irs) {
            this.dimensions = dimensions;
            this.irs.addAll(irs);
        }
    }
}
