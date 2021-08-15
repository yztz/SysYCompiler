package compiler;

import java.util.function.Supplier;

public class LazyGetter<T> {
    public T val = null;
    Supplier<T> supplier;

    public LazyGetter(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get()
    {
        if(val==null)
            val = supplier.get();
        return val;
    }
}
