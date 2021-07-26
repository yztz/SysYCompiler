package compiler.asm;

import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.Map;
import java.util.function.Function;

public class RegGetterPreAlloc extends RegGetter{

    @Override
    public Reg getRegOfAddress(InterRepresent ir, AddressOrData address) {
        return null;
    }

    @Override
    public Map<AddressRWInfo, Reg> getMapOfIR(InterRepresent ir) {
        return null;
    }

    @Override
    protected boolean isFreeReg(Reg reg) {
        return false;
    }
}
