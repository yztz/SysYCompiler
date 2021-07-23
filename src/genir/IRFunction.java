package genir;

import genir.code.InterRepresent;
import genir.code.InterRepresentHolder;
import symboltable.FuncSymbol;

import java.util.LinkedList;

public class IRFunction extends AbstractIR {
    private final LinkedList<IRSection> sections = new LinkedList<>();
    int nextGroupID = 0;
    private IRSection currentSection = null;
    public IRFunction() {
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
        tryFullFillVacancy();
    }

    public void insertGroupAfter(IRGroup group,IRGroup index)
    {
        currentSection.insertAfter(group,index);
    }

    public void insertGroupBefore(IRGroup group,IRGroup index)
    {
        currentSection.insertBefore(group,index);
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

        // 添加一节，需要把上一节未兑现的空位转移到新的节
        if(currentSection!=null)
            transferBookVacancy(currentSection,section);

        currentSection = section;

        tryFullFillVacancy();
    }

    private void transferBookVacancy(IRSection from,IRSection to)
    {
        to.vacancyHolders.addAll(from.vacancyHolders);
        from.vacancyHolders.clear();
    }

    private void tryFullFillVacancy()
    {
        if(currentSection.getLastIR()!=null)
            fullFillVacancy(currentSection.getLastIR());
        else{
            for (InterRepresentHolder holder : vacancyHolders) {
                currentSection.bookVacancy(holder);
            }
            vacancyHolders.clear();
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
}
