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
    LABEL,

    IF_ELSE,
    WHILE,
    CONTINUE,
    BREAK,
    RETURN,

//    CONST_DECL,
//    VAR_DECL,
//    FUNC_DECL,

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

    public static final OP[] STATEMENTS = {ASSIGN, IF_ELSE, WHILE, CALL, RETURN, GOTO};
//    public static final OP[] JUMPS = {GOTO, }

//    @Override
//    public String getVal() {
//        return this.name();
//    }
}
