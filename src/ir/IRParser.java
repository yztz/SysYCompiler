package ir;

import ast.*;
import common.ILabel;
import common.OffsetVar;
import common.symbol.Function;
import ir.code.*;
import common.OP;

import static ir.Temp.newTmp;

public class IRParser {
    public void flatAst(AstNode root) {
        PreProcessor.processWhile(root);
        PreProcessor.processIF(root);
        parseAst(root);
    }


    private IAstValue parseAst(AstNode root) {
        if (null == root) return null;
        if (root.label != null) IRs.setNextLabel(root.label);

        Temp tmp;
        IAstValue left, right;
        OP op = root.op;


        if (root.isLeaf()) {
            switch (op) {
                case STATEMENTS:
                case PARAM:
                    break;
                case RETURN:
                    IRs.addIR(new UnaryIR(op, null));
                    break;
                case FUNCTION:
                    if (!((Function) root.value).hasReturn()) {
                        IRs.addIR(new BinaryIR(OP.CALL, null, root.value));
                    } else {
                        tmp = newTmp();
                        IRs.addIR(new BinaryIR(OP.CALL, tmp, root.value));
                        return tmp;
                    }
                    break;
                case VAR_OFFSET:
                    AstNode offset = ((OffsetVar) root.value).offsetTree;
                    offset.value = parseAst(offset);
                    if (((OffsetVar) root.value).isLVal) {
                        return root.value;
                    } else {
                        tmp = Temp.newTmp();
                        IRs.addIR(new BinaryIR(OP.ASSIGN, tmp, root.value));
                        return tmp;
                    }
                case IMMEDIATE:
                    return root.value;
                case VARIABLE:
                    return root.value;
                case GOTO:
                    IRs.addIR(new GoToIR((ILabel) root.value));
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
//            case LE:
//            case LT:
//            case GE:
//            case GT:
//            case EQ:
//            case NOT_EQ:
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                IRs.addIR(new TernaryIR(op, tmp, left, right));
                return tmp;
            case MINUS:
            case NEGATE:
                tmp = newTmp();
                IAstValue sub = parseAst(root.getLeft());
                IRs.addIR(new BinaryIR(op, tmp, sub));
                return tmp;
            case ASSIGN:
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                IRs.addIR(new BinaryIR(op, left, right));
                break;
            case STATEMENTS:
                if (root.label instanceof Function) {
                    IRs.removeNextLabel();
                    IRs.startSection((Function) root.label);
                    for (AstNode child : root.getSubTrees()) parseAst(child);
                    IRs.endSection();
                } else {
                    for (AstNode child : root.getSubTrees()) parseAst(child);
                }
                break;
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
                    IRs.addIR(new UnaryIR(op, res));
                }
                break;
            case IF_ELSE:
                AstNode cond = root.getNode(0);
                AstNode then = root.getNode(1);
                AstNode el = root.getNode(2);
                IRs.addIR(new CondGoToIR(cond.op, (ILabel) cond.value, parseAst(cond.getLeft()), parseAst(cond.getRight())));
                parseAst(el);   // 先parse el方便ir跳转
                parseAst(then);
                break;
            case RETURN:
                left = parseAst(root.getLeft());
                IRs.addIR(new UnaryIR(OP.RETURN, left));
                break;
        }
        return null;

    }
}
