package compiler.symboltable.initvalue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class InitValue {
   public abstract long getLength();
   public abstract boolean isAllZero();
   public abstract long getLastNonZeroPos();
   public abstract void add(long pos,int val);
   public abstract int get(long pos);
}
