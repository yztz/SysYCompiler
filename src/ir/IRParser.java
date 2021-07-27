package ir;

import ast.*;
import common.ILabel;
import common.symbol.Function;
import common.symbol.Variable;
import ir.code.*;
import common.OP;

import java.util.*;

import static ir.Temp.newTmp;

public class IRParser {
    private IRs irs = new IRs();

    public IRs flatAst(AstNode root) {
        PreProcessor.processWhile(root);
        PreProcessor.processIF(root);
        parseAst(root);
        return irs;
    }


    private IAstValue parseAst(AstNode root) {
        if (null == root) return null;
        if (root.label != null) irs.setNextLabel(root.label);

        Temp tmp;
        IAstValue left, right;
        OP op = root.op;


        if (root.isLeaf()){
            switch (op) {
                case STATEMENTS:
                case PARAM:
                    break;
                case RETURN:
                    irs.addIR(new UnaryIR(op, null));
                    break;
                case FUNCTION:
                    if (!((Function) root.value).hasReturn()) {
                        irs.addIR(new UnaryIR(OP.CALL, root.value));
                    } else {
                        tmp = newTmp();
                        irs.addIR(new BinaryIR(OP.CALL, tmp, root.value));
                        return tmp;
                    }
                    break;
                case VAR_OFFSET:
                    AstNode offset = ((OffsetVar)root.value).offset;
                    offset.value = parseAst(offset);
                    return root.value;
                case IMMEDIATE:
                    return root.value;
                case VARIABLE:
                    return root.value;
                case GOTO:
                    irs.addIR(new GoToIR((ILabel) root.value));
                    break;
            }
            return null;
        }

        switch (op) {
            case MUL:
            case DIV:
            case MOD:
            case SUB:
            case ADD:
            case LE:
            case LT:
            case GE:
            case GT:
            case EQ:
            case NOT_EQ:
            case AND:
            case OR:
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                irs.addIR(new TernaryIR(op, tmp, left, right));
                return tmp;
            case MINUS:
            case NEGATE:
                tmp = newTmp();
                IAstValue sub = parseAst(root.getLeft());
                irs.addIR(new BinaryIR(op, tmp, sub));
                return tmp;
            case ASSIGN:
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                irs.addIR(new BinaryIR(op, left, right));
                break;
            case STATEMENTS:
            case ROOT:
            case ASSIGN_GROUP:
                for (AstNode child : root.getSubTrees()) parseAst(child);
                break;
            case CALL:
                parseAst(root.getLeft());
                return parseAst(root.getRight());
            case PARAM:
                for (AstNode child : root.getSubTrees()) {
                    IAstValue res = parseAst(child);
                    irs.addIR(new UnaryIR(op, res));
                }
                break;
            case IF_ELSE:
                AstNode cond = root.getNode(0);
                AstNode then = root.getNode(1);
                AstNode el = root.getNode(2);
                irs.addIR(new CondGoToIR((ILabel) cond.value, parseAst(cond.getLeft()), cond.op, parseAst(cond.getRight())));
                parseAst(el);   // 先parse el方便ir跳转
                parseAst(then);
                break;
            case RETURN:
                left = parseAst(root.getLeft());
                irs.addIR(new UnaryIR(OP.RETURN, left));
                break;
        }
        return null;

    }
}
