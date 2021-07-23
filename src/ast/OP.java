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
    ROOT,
    CONST_DECL,
    VAR_DECL,
    PARAM,
    RETURN,
    CONTINUE,
    BREAK,
    FUNC_DECL,

    NEGATE,
    LE,
    LT,
    GE,
    GT,
    EQ,
    NOT_EQ,
    AND,
    CALL,
    OR;


    @Override
    public String getVal() {
        return this.name();
    }
}
