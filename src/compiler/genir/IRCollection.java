package compiler.genir;


import compiler.genir.code.InterRepresent;
import compiler.genir.code.InterRepresentHolder;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;

public class IRCollection {
    
    public IRCollection parent;
    private int id;
    public int getID() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    protected final List<InterRepresentHolder> vacancyHolders =new ArrayList<>();
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

    private final LinkedList<InterRepresent> irs = new LinkedList<>();

    private final Map<Integer,String> descriptionMap = new HashMap<>();

    /**
     * 开始新的一节代码，并设置描述
     */
    public void startSection(String description)
    {
        descriptionMap.put(getLineOccupied(),description);
    }


    public void insertBefore(InterRepresent inserted, InterRepresent benchmark,String description)
    {
        startSection(description);
        insertBefore(inserted,benchmark);
    }

    public void insertBefore(InterRepresent inserted, InterRepresent benchmark)
    {
        inserted.collection = this;
        int index= irs.indexOf(benchmark);
        irs.stream().parallel().forEach(t->{
            if (t.getLineNum()>=index)
                t.setLineNum(t.getLineNum()+1);
        });
        inserted.setLineNum(index);
        irs.add(index,inserted);
        nextLineNum++;
    }

    int nextLineNum = 0;
    public void addCode(InterRepresent ir)
    {
        ir.collection = this;
        irs.add(ir);
        ir.setLineNum(nextLineNum);
        nextLineNum++;
        fullFillVacancy(ir);
    }

    public void addCode(InterRepresent ir, String description)
    {
        startSection(description);
        addCode(ir);
    }

    public void addCodes(IRCollection irs)
    {
        for (InterRepresent ir : irs.irs) {
            addCode(ir);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (InterRepresent ir : irs) {
            sb.append(ir.toString()).append("\r\n");
        }
        return sb.toString();
    }
    public InterRepresent getFirst(){return irs.getFirst();}
    public InterRepresent getLast()
    {
        return irs.getLast();
    }


    public int getLineOccupied() {
        return irs.size();
    }

    public List<InterRepresent> flatIR() {

        return irs;
    }
}
