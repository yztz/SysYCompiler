package gencode;

import genir.IRCode;
import genir.code.InterRepresent;

public interface RegisterGetter {
    Register getRegister(Address address, InterRepresent ir);

}
