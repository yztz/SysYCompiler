package compiler.symboltable.initvalue;

public class NonInitValue extends InitValue{
    long length;

    public NonInitValue(long length) {
        this.length = length;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public boolean isAllZero() {
        return false;
    }

    @Override
    public long getLastNonZeroPos() {
        return 0;
    }

    @Override
    public void add(long pos, int val) {

    }

    @Override
    public int get(long pos) {
        return 0;
    }
}
