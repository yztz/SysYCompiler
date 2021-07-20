package gencode.code.operand;

import gencode.code.interfaces.Location;
import gencode.code.interfaces.RegOrImm;

public class Immediate implements RegOrImm {
    public final int value;

    public Immediate(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "#" + value;
    }
}
