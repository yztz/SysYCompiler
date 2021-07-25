package compiler.asm;

import java.util.Objects;

public class Reg {
    int id;

    public int getId() {
        return id;
    }

    public Reg(int id) {
        this.id = id;
    }

    public String getText() {
        if(id==11)
            return "fp";
        else if(id == 13)
            return "sp";
        else if(id == 14)
            return "lr";
        else if(id ==15)
            return "pc";
        return String.format("r%d",id);
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
