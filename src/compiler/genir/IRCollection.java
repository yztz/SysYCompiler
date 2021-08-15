package compiler.genir;


import compiler.Location;
import compiler.genir.code.GotoRepresent;
import compiler.genir.code.InterRepresent;
import compiler.genir.code.InterRepresentHolder;
import org.antlr.v4.runtime.Token;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    protected final LinkedList<InterRepresent> irs = new LinkedList<>();

    private final Map<Integer,String> descriptionMap = new HashMap<>();

    /**
     * 开始新的一节代码，并设置描述
     */
    public void startSection(String description)
    {
        descriptionMap.put(getLineOccupied(),description);
    }

    public String getDescription(int lineNum)
    {
        return descriptionMap.getOrDefault(lineNum,"");
    }

    public void insertBefore(InterRepresent inserted, InterRepresent benchmark,String description)
    {
        startSection(description);
        insertBefore(inserted,benchmark);
    }

    public void insertBefore(InterRepresent inserted, InterRepresent benchmark)
    {
        insertBefore(inserted,benchmark,false);
    }

    public void insertBefore(InterRepresent inserted, InterRepresent benchmark,boolean replaceGotoTarget)
    {
        inserted.collection = this;
        int index= irs.indexOf(benchmark);
        irs.stream().parallel().forEach(t->{
            if(replaceGotoTarget && (t instanceof GotoRepresent))
            {
                GotoRepresent gotoIR = (GotoRepresent) t;
                if (gotoIR.targetHolder!=null &&
                        gotoIR.targetHolder.getInterRepresent()!=null
                        && gotoIR.targetHolder.getInterRepresent() == benchmark) {
                    gotoIR.targetHolder.setInterRepresent(inserted);
                }
            }

            if (t.getLineNum()>=index)
                t.setLineNum(t.getLineNum()+1);
        });
        inserted.setLineNum(index);
        irs.add(index,inserted);
        nextLineNum++;
    }

    int nextLineNum = 0;
    public void addCode(InterRepresent ir, Token token)
    {
        addCode(ir);
        if(token==null)
            return;
        ir.location = new Location(token);
    }

    public void addCode(InterRepresent ir,Token token, String description)
    {
        startSection(description);
        addCode(ir,token);
    }

    private void addCode(InterRepresent ir)
    {
        ir.collection = this;
        irs.add(ir);
        ir.setLineNum(nextLineNum);
        nextLineNum++;
        fullFillVacancy(ir);
    }

    public void addCodes(IRCollection irs)
    {
        for (InterRepresent ir : irs.getAllIR()) {
            addCode(ir);
        }
    }

    public void remove(InterRepresent ir)
    {
        int index = irs.indexOf(ir);
        if(index>=0)
        irs.remove(index);
        int i = 0;
        for (InterRepresent item : irs) {
            if(i>=index)
            {
                item.lineNum--;
            }
            i++;
        }
    }

    public Stream<InterRepresent> findAll(Predicate<InterRepresent> predicate)
    {
        return irs.stream().filter(predicate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int line = 0;
        for (InterRepresent ir : irs) {
            sb.append(ir.toString());
            if(descriptionMap.containsKey(line))
            {
                sb.append("\t@").append(descriptionMap.get(line));
            }
            line++;
            sb.append("\r\n");
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

    public List<InterRepresent> getAllIR() {

        return irs;
    }
}
