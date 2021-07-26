package compiler.genir.code;

import compiler.asm.AddressRWInfo;

import java.util.Collection;

public class IfGotoRepresent extends GotoRepresent {

    public IfGotoRepresent(InterRepresent target, RelOp relOp, AddressOrData left, AddressOrData right) {
        super(target);
        this.relOp = relOp;
        this.left = left;
        this.right = right;
    }

    public RelOp relOp;
    public AddressOrData left;
    public AddressOrData right;

    @Override
    public String toString() {
        InterRepresent targetIR = targetHolder.getInterRepresent();
        return String.format("%s: if %s %s %s goto %s",lineNumToString(), left, relOp, right,targetIR==null?-1:
                targetIR.lineNumToString() );
    }

    @Override
    public Collection<AddressRWInfo> getAllAddressRWInfo() {
        Collection<AddressRWInfo> allAddressRWInfos = super.getAllAddressRWInfo();
        if(!left.isData)
            allAddressRWInfos.add(new AddressRWInfo(left));

        if(!right.isData)
            allAddressRWInfos.add(new AddressRWInfo(right));

        return allAddressRWInfos;
    }

    public enum RelOp{
        LESS,
        GREATER,
        LESS_EQUAL,
        GREATER_EQUAL,
        EQUAL,
        NOT_EQUAL;

        /**
         * 左右交换
         */
        public RelOp getReverse()
        {
            switch (this) {
                case LESS:
                    return GREATER;
                case GREATER:
                    return LESS;
                case LESS_EQUAL:
                    return GREATER_EQUAL;
                case GREATER_EQUAL:
                    return LESS_EQUAL;
                case EQUAL:
                    return EQUAL;
                case NOT_EQUAL:
                    return NOT_EQUAL;
            }
            return NOT_EQUAL;
        }
    }
}
