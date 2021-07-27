package compiler.genir.code;



public class InterRepresentHolder {
    public InterRepresentHolder(){

    }
    public InterRepresentHolder(InterRepresent interRepresent) {
        this.interRepresent = interRepresent;
    }

    
    private InterRepresent interRepresent;

    public InterRepresent getInterRepresent() {
        return interRepresent;
    }

    public void setInterRepresent(InterRepresent interRepresent) {
        this.interRepresent = interRepresent;
    }
}
