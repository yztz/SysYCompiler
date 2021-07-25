package compiler.genir.code;

import com.sun.istack.internal.Nullable;

public class InterRepresentHolder {
    public InterRepresentHolder(){

    }
    public InterRepresentHolder(InterRepresent interRepresent) {
        this.interRepresent = interRepresent;
    }

    @Nullable
    private InterRepresent interRepresent;

    public InterRepresent getInterRepresent() {
        return interRepresent;
    }

    public void setInterRepresent(InterRepresent interRepresent) {
        this.interRepresent = interRepresent;
    }
}
