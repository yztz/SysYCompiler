package ast;

public enum OP implements AstValue {
    ADD,
    MINUS,
    SUB,
    MUL,
    DIV,
    MOD,
    IF_ELSE,
    WHILE,
    ASSIGN,
    OFFSET,
    STATEMENT,
    VAL_GROUP,
    NEGATE;


    @Override
    public String getVal() {
        return this.name();
    }
}
