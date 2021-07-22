package ast;

public enum OP implements Value{
    ADD,
    MINUS,
    SUB,
    MUL,
    DIV,
    MOD,
    IF_ELSE,
    WHILE,
    ASSIGN,
    NEGATE;

    @Override
    public String getVal() {
        return this.name();
    }
}
