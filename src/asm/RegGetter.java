package asm;

import genir.code.AddressOrData;
import genir.code.InterRepresent;

import java.util.Map;

public interface RegGetter {
     Reg getRegOfAddress(InterRepresent ir, AddressOrData address);
     Map<Address, Reg> getMapOfIR(InterRepresent ir);
     Map<Address, Reg> getVarDesc();
     Map<Reg, Address> getRegDesc();
     Reg getTmpRegister();
}
