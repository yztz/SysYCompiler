package genir;

import genir.code.InterRepresent;
import genir.code.InterRepresentHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IRCode {
    private int nextLineNum = 0;
    public LinkedList<InterRepresent> codes = new LinkedList<>();
    public void addCode(InterRepresent code)
    {
        code.setLineNum(nextLineNum);
        nextLineNum++;
        codes.addLast(code);
        fullFillVacancy(code);
    }

    private final List<InterRepresentHolder> vacancyHolders =new ArrayList<>();

    /**
     * 预定一个空位，当添加一条新的代码时，将这些空位回填
     * @param holder 用来保存该代码的holder
     */
    public void bookVacancy(InterRepresentHolder holder)
    {
        vacancyHolders.add(holder);
    }
    public void fullFillVacancy(InterRepresent ir)
    {
        for (InterRepresentHolder holders : vacancyHolders) {
            holders.setInterRepresent(ir);
        }
        vacancyHolders.clear();
    }
    public void fillVacancy(InterRepresentHolder holder, int irIndex)
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
        nextLineNum++;
        //fullFillQuad(code);
    }
    public int getNextLineNum() {
        return nextLineNum;
    }
}
