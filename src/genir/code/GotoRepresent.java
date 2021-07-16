package genir.code;

public class GotoRepresent extends InterRepresent{
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
        return lineNumToString()+String.format("goto %-7d",target==null?-1: target.lineNum);
    }
}
