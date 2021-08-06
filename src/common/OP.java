package common;

import ast.AstNode;

public enum OP {
    ADD,
    MINUS,
    SUB,
    MUL,
    DIV,
    MOD,
    ASSIGN,
    ASSIGN_GROUP,

    STATEMENTS,
    VAL_GROUP,
    ROOT,

    CALL,
    PARAM,
    COND_GOTO,
    GOTO,

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

    public static final OP[] COMMUTATIVE_OP = {
            ADD,
            MUL,
            EQ,
            NOT_EQ
    };

    public static final OP[] REL_OP = {
            //NEGATE,
            LE,
            LT,
            GE,
            GT,
            EQ,
            NOT_EQ,
            AND,
            OR,
    };

    public static boolean include(OP op, OP[] ops) {
        for (OP tmp : ops) {
            if (op == tmp) return true;
        }
        return false;
    }

    public static boolean isCommutative(OP op) {
        return include(op, COMMUTATIVE_OP);
    }


    /**
     * 判断是否是关系运算
     */
    public static boolean isRelOP(OP op) {
        return include(op, REL_OP);
    }



}
