package compiler.genir;

import compiler.genir.code.InterRepresent;

import java.util.ArrayList;
import java.util.List;

public class IRBlock extends IRCollection {
    public String label;

    public IRBlock prevBlock;
    public IRBlock nextBlock;

    public List<IRBlock> possibleGotoBlock = new ArrayList<>();
    public List<IRBlock> mustGotoBlock = new ArrayList<>();

    public IRBlock(String label) {
        this.label = label;
    }

    public void addIR(InterRepresent ir)
    {
        irs.add(ir);
    }

    public List<InterRepresent> irs = new ArrayList<>();

    public void moveDown(int originIndex, int targetIndex)
    {
        InterRepresent ir = irs.get(originIndex);
        irs.remove(originIndex);
        irs.add(targetIndex,ir);
    }

    @Override
    public int getLineOccupied() {
        return irs.size();
    }

    @Override
    public List<InterRepresent> flatIR() {
        return irs;
    }
}
