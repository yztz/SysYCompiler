package ast;

public enum OP implements AstValue {
    ADD,
    MINUS,
    SUB,
    MUL,
    DIV,
    MOD,
    ASSIGN,
    OFFSET,

    STATEMENT,
    VAL_GROUP,
    ROOT,

    CALL,
    PARAM,

    IF_ELSE,
    WHILE,
    CONTINUE,
    BREAK,
    RETURN,

    CONST_DECL,
    VAR_DECL,
    FUNC_DECL,

    NEGATE,
    LE,
    LT,
    GE,
    GT,
    EQ,
    NOT_EQ,
    AND,
    OR;



    @Override
    public String getVal() {
        return this.name();
    }
}
