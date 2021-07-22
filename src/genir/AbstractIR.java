package genir;

import com.sun.istack.internal.Nullable;
import genir.code.InterRepresent;
import genir.code.InterRepresentHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIR {
    @Nullable
    public AbstractIR parent;
    private int id;
    public int getID() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 一共有多少行代码
     * @return 代码行数
     */
    public abstract int getLineOccupied();

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
}
