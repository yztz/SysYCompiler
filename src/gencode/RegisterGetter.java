package gencode;

import genir.IRCode;
import genir.code.InterRepresent;

import java.util.Map;

public interface RegisterGetter {
     Map<Address, Register> getRegister(IRCode irCode);
}
