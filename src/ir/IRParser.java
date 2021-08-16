package ir;

import ast.*;
import common.ILabel;
import common.OffsetVar;
import common.Temp;
import common.symbol.Function;
import ir.code.*;
import common.OP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.Temp.newTmp;

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
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                // a * 1, 1 * a, 0 * a, a * 0
                if (left.getVal().equals(0) || right.getVal().equals(0)) {
                    return new Immediate(0);
                } else if (left.getVal().equals(1)) {
                    return right;
                } else if (right.getVal().equals(1)) {
                    return left;
                } else {
                    IRs.addIR(new TernaryIR(op, tmp, left, right));
                    return tmp;
                }
            case DIV:
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                // 0 / a, a / a, a / 1
                if (left.getVal().equals(0)) {
                    return new Immediate(0);
                } else if (left == right) {
                    return new Immediate(1);
                } else if (right.getVal().equals(1)) {
                    return left;
                } else {
                    IRs.addIR(new TernaryIR(op, tmp, left, right));
                    return tmp;
                }
            case MOD:
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                // a % 1 = 0, a % a = 0, 0 % a = 0
                if (left.getVal().equals(0) || right.getVal().equals(1) || left == right) {
                    return new Immediate(0);
                } else {
                    IRs.addIR(new TernaryIR(op, tmp, left, right));
                    return tmp;
                }
            case SUB:
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                // a - 0 = a; a - a = 0;
                if (right.getVal().equals(0)) {
                    return left;
                } else if (left == right) {
                    return new Immediate(0);
                } else {
                    IRs.addIR(new TernaryIR(op, tmp, left, right));
                    return tmp;
                }
            case ADD:
                tmp = newTmp();
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                // any + 0, 0 + any
                if (left.getVal().equals(0)) {
                    return right;
                } else if (right.getVal().equals(0)) {
                    return left;
                } else {
                    IRs.addIR(new TernaryIR(op, tmp, left, right));
                    return tmp;
                }
            case LE:
            case LT:
            case GE:
            case GT:
            case EQ:
            case NOT_EQ:
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
                List<AstNode> children = root.getSubTrees();
                Map<AstNode, IAstValue> results = new HashMap<>();
                for (int i = 0; i < children.size(); i++) {
                    AstNode child = children.get(i);
                    if (child.op == OP.CALL) {
                        tmp = (Temp) parseAst(child);
                        tmp.paramIdx = i;
                        results.put(child, tmp);
                    }
                }
                for (AstNode child : root.getSubTrees()) {
                    IAstValue res;
                    if (results.containsKey(child))
                        res = results.get(child);
                    else
                        res = parseAst(child);
                    IRs.addIR(new UnaryIR(op, res));
                }
                break;
            case IF_ELSE:
                AstNode cond = root.getNode(0);
                AstNode then = root.getNode(1);
                AstNode el = root.getNode(2);
                left = parseAst(cond.getLeft());
                right = parseAst(cond.getRight());
                switch (cond.op) {
                    case GE:
                        op = OP.GE_GOTO;
                        break;
                    case GT:
                        op = OP.GT_GOTO;
                        break;
                    case LE:
                        op = OP.LE_GOTO;
                        break;
                    case LT:
                        op = OP.LT_GOTO;
                        break;
                    case EQ:
                        op = OP.EQ_GOTO;
                        break;
                    case NOT_EQ:
                        op = OP.NOT_EQ_GOTO;
                        break;
                    default:
                        System.err.println("unknown relOP" + op);
                }
                IRs.addIR(new CondGoToIR(op, (ILabel) cond.value, left, right));
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
