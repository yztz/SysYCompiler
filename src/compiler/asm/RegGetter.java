package compiler.asm;

import compiler.genir.code.AddressOrData;
import compiler.genir.code.InterRepresent;

import java.util.Map;
import java.util.function.Function;

public interface RegGetter {
     Reg getRegOfAddress(InterRepresent ir, AddressOrData address);
     Map<Address, Reg> getMapOfIR(InterRepresent ir);
     Map<Address, Reg> getVarDesc();
     Map<Reg, Address> getRegDesc();
     Reg getTmpRegister();
     Reg getTmpRegister(int index);
     Reg getTmpRegister(Function<Reg,Boolean> rule, int index);
}
