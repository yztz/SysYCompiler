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
    LOAD,

    STATEMENTS,
    VAL_GROUP,
    ROOT,

    CALL,
    PARAM,
//    COND_GOTO,
    GOTO,
    GT_GOTO,
    GE_GOTO,
    LT_GOTO,
    LE_GOTO,
    EQ_GOTO,
    NOT_EQ_GOTO,

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

    public static final OP[] JUMP_OP = {
            GOTO,
            GT_GOTO,
            GE_GOTO,
            LT_GOTO,
            LE_GOTO,
            EQ_GOTO,
            NOT_EQ_GOTO,
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

    public static boolean isJump(OP op) {
        return include(op, JUMP_OP);
    }

    /**
     * 判断是否是关系运算
     */
    public static boolean isRelOP(OP op) {
        return include(op, REL_OP);
    }





}
