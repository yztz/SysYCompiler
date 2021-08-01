package compiler.symboltable.initvalue;

public class SingleInitValue extends InitValue{
    int val;

    public SingleInitValue(int val) {
        this.val = val;
    }

    public SingleInitValue() {
    }

    @Override
    public long getLength() {
        return 1;
    }

    @Override
    public boolean isAllZero() {
        return val==0;
    }

    @Override
    public long getLastNonZeroPos() {
        return val!=0?0:-1;
    }

    @Override
    public void add(long pos, int val) {
        this.val = val;
    }

    @Override
    public int get(long pos) {
        return val;
    }
}
