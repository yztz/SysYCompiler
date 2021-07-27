package ast;

public interface IAstValue {
    default Object getVal(){
        return this;
    }
}
