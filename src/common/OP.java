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

    //    public static final OP[] STATEMENT = {ASSIGN, IF_ELSE, WHILE, PARAM, FUNCTION, RETURN, GOTO};
    public static final OP[] STATEMENT = {
            ASSIGN,
            IF_ELSE,
            WHILE,
            PARAM,
            FUNCTION,
            RETURN,
            GOTO,
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


    /**
     * 判断当前节点是否是语句
     */
    public static boolean isStatement(AstNode node) {
        if (null == node) return false;
        return include(node.op, STATEMENT);
    }

    /**
     * 判断是否是关系运算
     */
    public static boolean isRelOP(AstNode node) {
        if (null == node) return false;
        return include(node.op, REL_OP);
    }


}
