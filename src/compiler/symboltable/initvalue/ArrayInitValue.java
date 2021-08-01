package compiler.symboltable.initvalue;

import java.util.HashMap;
import java.util.Map;

public class ArrayInitValue extends InitValue{
    public ArrayInitValue(long length) {
        this.length = length;
    }

    public long length;
    public Map<Integer,Long> positions = new HashMap<>();
    public Map<Long,Integer> vals = new HashMap<>();

    private long lastNonZeroPos = -1;
    public void add(long pos,int val)
    {
        if(val!=0)
        {
            lastNonZeroPos = Math.max(lastNonZeroPos,pos);
        }
        vals.put(pos,val);
        positions.put(val,pos);
    }

    @Override
    public int get(long pos) {
        return vals.getOrDefault(pos,0);
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public boolean isAllZero() {
        return lastNonZeroPos==-1;
    }

    @Override
    public long getLastNonZeroPos() {
        return lastNonZeroPos;
    }
}
