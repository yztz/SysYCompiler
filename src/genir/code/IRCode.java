package genir.code;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class IRCode {
    private int nextQuad = 0;
    public LinkedList<InterRepresent> codes = new LinkedList<>();
    public void addCode(InterRepresent code)
    {
        code.setLineNum(nextQuad);
        nextQuad++;
        codes.addLast(code);
        fullFillQuad(code);
    }

    private final List<InterRepresentHolder> bookQuadHolders=new ArrayList<>();

    /**
     * 预定一个空位，当添加一条新的代码时，将这些空位回填
     * @param holder 用来保存该代码的holder
     */
    public void bookQuad(InterRepresentHolder holder)
    {
        bookQuadHolders.add(holder);
    }
    public void fullFillQuad(InterRepresent ir)
    {
        for (InterRepresentHolder bookQuadHolder : bookQuadHolders) {
            bookQuadHolder.setInterRepresent(ir);
        }
        bookQuadHolders.clear();
    }
    public void fillQuad(InterRepresentHolder holder,int irIndex)
    {
        holder.setInterRepresent(codes.get(irIndex));
    }
    public InterRepresent getInterRepresent(int index)
    {
        return codes.get(index);
    }
    public void insertCode(int index,InterRepresent code)
    {
        codes.stream().parallel().forEach(t->{
            if (t.lineNum>=index)
                t.lineNum=t.lineNum+1;
        });
        code.setLineNum(index);
        codes.add(index,code);
        nextQuad++;
        //fullFillQuad(code);
    }
    public int getNextQuad() {
        return nextQuad;
    }
}
