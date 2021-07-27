package asm;

import ast.AstNode;
import common.OP;

public class CodeGenerator {
    RegGetter regGetter = new RegGetterImpl();

    public CodeGenerator() {

    }

    public void genCode(AstNode root) {
        OP op = root.op;
        if (root.isLeaf()) {
            switch (root.op){
                case MINUS:
                    break;

                case PARAM:
                    break;
                case STATEMENTS:
                    break;

                case CONTINUE:
                    break;

                case BREAK:
                    break;

                case RETURN:
                    break;


                case FUNCTION:
                    break;

                case IMMEDIATE:
                    break;

                case VARIABLE:
                    break;
            }
        }


        switch (op){
            case ADD:
                break;
            case MINUS:
                break;
            case SUB:
                break;
            case MUL:
                break;
            case DIV:
                break;
            case MOD:
                break;
            case ASSIGN:
                break;
            case STATEMENTS:
                break;
            case VAL_GROUP:
                break;
            case ROOT:
                break;
            case CALL:
                break;
            case PARAM:
                break;
            case IF_ELSE:
                break;
            case WHILE:
                break;
            case CONTINUE:
                break;
            case BREAK:
                break;
            case RETURN:
                break;
            case FUNCTION:
                break;
            case IMMEDIATE:
                break;
            case VARIABLE:
                break;
            case NEGATE:
                break;
            case LE:
                break;
            case LT:
                break;
            case GE:
                break;
            case GT:
                break;
            case EQ:
                break;
            case NOT_EQ:
                break;
            case AND:
                break;
            case OR:
                break;
        }
    }


}
