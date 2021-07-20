package gencode.code.operand;

import gencode.code.interfaces.Location;
import gencode.code.interfaces.Memory;

public class LocWithOffset implements Memory {
    public final Register base;
    public final Location offset;

    public LocWithOffset(Register base, Location offset) {
        this.base = base;
        this.offset = offset;
    }

    public LocWithOffset(Register base, int offset) {
        this.base = base;
        this.offset = new Immediate(offset);
    }

    @Override
    public String toString() {
        if (offset instanceof Immediate && ((Immediate) offset).value == 0)
            return String.format("[%s]", base);
        else
            return String.format("[%s, %s]", base, offset);
    }
}
