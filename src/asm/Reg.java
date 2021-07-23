package asm;

import java.util.Locale;
import java.util.Objects;

public class Reg {
    int id;

    public Reg(int id) {
        this.id = id;
    }

    public String getText() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reg reg = (Reg) o;
        return id == reg.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
