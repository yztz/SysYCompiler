package asm;

import common.symbol.Variable;

public interface RegGetter {
    Register getReg(Variable var);
    Register getReg();
    void freeReg(Register register);
}
