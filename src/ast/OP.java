package ast;

public enum OP  {
    ADD,
    MINUS,
    SUB,
    MUL,
    DIV,
    MOD,
    ASSIGN,
    ASSIGN_GROUP,

    STATEMENT,
    VAL_GROUP,
    ROOT,

    CALL,
    PARAM,
    GOTO,

    IF_ELSE,
    WHILE,
    CONTINUE,
    BREAK,
    RETURN,

    CONST_DECL,
    VAR_DECL,
    FUNC_DECL,

    FUNCTION,
    IMMEDIATE,
    VARIABLE,
    VAR_OFFSET,

    NEGATE,
    LE,
    LT,
    GE,
    GT,
    EQ,
    NOT_EQ,
    AND,
    OR;



//    @Override
//    public String getVal() {
//        return this.name();
//    }
}
