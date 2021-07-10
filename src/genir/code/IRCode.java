package genir.code;

import java.util.LinkedList;

public class IRCode {
    private int nextQuad = 0;
    public LinkedList<InterRepresent> codes = new LinkedList<>();
    public void addCode(InterRepresent code)
    {
        code.setLineNum(nextQuad);
        nextQuad++;
        codes.addLast(code);
    }

    public int getNextQuad() {
        return nextQuad;
    }
}
