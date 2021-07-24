package genir;

import genir.code.InterRepresent;

import java.util.ArrayList;
import java.util.List;

public class IRBlock extends AbstractIR{
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

    @Override
    public int getLineOccupied() {
        return irs.size();
    }

    @Override
    public List<InterRepresent> flatIR() {
        return irs;
    }
}
