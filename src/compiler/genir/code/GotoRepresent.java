package compiler.genir.code;

import compiler.asm.AddressRWInfo;

import java.util.ArrayList;
import java.util.Collection;

public class GotoRepresent extends InterRepresent{
    public int flag;
    public InterRepresentHolder targetHolder;

    public GotoRepresent(InterRepresent target) {
        this.targetHolder =new InterRepresentHolder(target);
    }
    public void setTargetIR(InterRepresent targetHolder)
    {
        this.targetHolder.setInterRepresent(targetHolder);
    }
    public InterRepresent getTargetIR()
    {
        return targetHolder.getInterRepresent();
    }
    @Override
    public String toString() {
        InterRepresent target = getTargetIR();
        return String.format("%s: goto %-7s",lineNumToString(),target==null?"NULL": target.lineNumToString());
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        return new ArrayList<>();
    }

    @Override
    public InterRepresent createCopy() {
        GotoRepresent gotoRepresent = new GotoRepresent(null);
        gotoRepresent.targetHolder = targetHolder;
        return gotoRepresent;
    }
}
