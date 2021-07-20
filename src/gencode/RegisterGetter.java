package gencode;

import gencode.code.operand.Register;
import genir.code.InterRepresent;

import java.util.Map;

public interface RegisterGetter {
     Map<Address, Register> getMapOfIR(InterRepresent ir);
     Map<Address, Register> getVarDesc();
     Map<Register, Address> getRegDesc();
}
