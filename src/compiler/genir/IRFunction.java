package compiler.genir;

import compiler.genir.code.InterRepresent;
import compiler.genir.code.InterRepresentHolder;
import compiler.symboltable.function.FuncSymbol;

import java.util.*;

public class IRFunction extends AbstractIR{
    private final LinkedList<IRSection> sections = new LinkedList<>();
    int nextGroupID = 0;
    private IRSection currentSection = null;
    public FuncSymbol funcSymbol;

    public IRFunction(FuncSymbol funcSymbol) {
        this.funcSymbol = funcSymbol;
    }

    /**
     * 开始新的一节代码，并设置描述
     */
    public void startSection(String description)
    {
        startSection(new IRSection(description));
    }

    public void endSection()
    {
        this.vacancyHolders.addAll(currentSection.vacancyHolders);
        currentSection.vacancyHolders.clear();
        currentSection = null;
    }

    /**
     * 该节结束，同时设置描述(会覆盖startSection时设置的描述)
     */
    public void endSection(String description)
    {
        if(currentSection==null)
            return;
        currentSection.description = description;
        this.vacancyHolders.addAll(currentSection.vacancyHolders);
        currentSection.vacancyHolders.clear();
        currentSection = null;
    }



    private void startDefaultSection()
    {
        startSection(new IRSection("function stmt"));
    }

    public void addGroup(IRGroup group)
    {
        if(currentSection ==null)
            startDefaultSection();

        currentSection.addGroup(group);
        fullFillVacancy();
    }

    public void insertGroupAfter(IRGroup group,IRGroup benchmark)
    {
        benchmark.getSection().insertAfter(group,benchmark);
    }

    public void insertGroupBefore(IRGroup group,IRGroup benchmark)
    {
        benchmark.getSection().insertBefore(group,benchmark);

    }

    public void split(IRGroup index,String desc1,String desc2)
    {
        IRSection[] split = currentSection.split(index, desc1, desc2);
        sections.remove(currentSection);
        nextGroupID--;
        startSection(split[0]);
        startSection(split[1]);
    }

    public void addSingleIR(InterRepresent ir,String description)
    {
        IRGroup irGroup =new IRGroup(description);
        irGroup.addCode(ir);
        addGroup(irGroup);
        fullFillVacancy();
    }

    public void startSection(IRSection section)
    {
        if(sections.size()>0 && getLast().getLineOccupied()==0)
        {
            sections.remove(getLast());
            nextGroupID--;
        }
        section.setId(nextGroupID++);
        sections.add(section);


        currentSection = section;

    }

    private Map<InterRepresentHolder,Integer> vacancyBooker = new HashMap<>();

    @Override
    public void bookVacancy(InterRepresentHolder holder) {
        vacancyBooker.put(holder,getLineOccupied());
    }

    public void fullFillVacancy() {
        List<InterRepresent> irs = flatIR();
        HashSet<InterRepresentHolder> done=new HashSet<>();
        for (InterRepresentHolder holder : vacancyBooker.keySet()) {
            int wantLineNum =  vacancyBooker.get(holder);
            if(irs.size()>wantLineNum)
            {
                holder.setInterRepresent(irs.get(wantLineNum));
                done.add(holder);
            }
        }
        for (InterRepresentHolder holder : done) {
            vacancyBooker.remove(holder);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRSection section : sections) {
            sb.append(section.toString()).append("\r\n");
        }
        return sb.toString();
    }

    public IRSection getLast()
    {
        return sections.getLast();
    }


    @Override
    public int getLineOccupied() {
        int totalOccupied = 0;
        for (AbstractIR ir : sections) {
            totalOccupied+=ir.getLineOccupied();
        }
        return totalOccupied;
    }

    @Override
    public List<InterRepresent> flatIR() {
        List<InterRepresent> irs = new ArrayList<>();
        for (IRSection section : sections) {
            irs.addAll(section.flatIR());
        }

        return irs;
    }


}
