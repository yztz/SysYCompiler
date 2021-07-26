package ir;

import ast.AstNode;
import ast.AstValue;
import ast.OffsetVar;
import common.symbol.Domain;
import common.symbol.Function;
import common.symbol.Variable;
import ir.code.*;
import ast.OP;

import java.util.*;

import static ir.Temp.newTmp;

public class AstParser {
    private List<IR> irs = new LinkedList<>();
    private Deque<Label> trueLabels = new ArrayDeque<>();
    private Deque<Label> falseLabels = new ArrayDeque<>();
    private Label label;
    
    private IR addIR(IR ir) {
        if (label != null && label.getIR() == null) {
            ir.label = label;
//            System.out.println(ir);
            label.bindIR(ir);
        }
        irs.add(ir);

        return ir;
    }

    private void setNextLabel(Label label) {
        this.label = label;
    }

    public List<IR> getIRCode() {
        return irs;
    }


    public AstValue parseAst(AstNode root) {
        if (null == root) return null;

        Temp tmp;
        AstValue left, right, cond;
        Label trueLabel, falseLabel;
        OP op = root.op;


        if (root.isLeaf()){
            switch (op) {
                case STATEMENT:
                case PARAM:
                    break;
                case CONTINUE:
                    addIR(new GoToIR(trueLabels.peek()));
                    break;
                case BREAK:
                    addIR(new GoToIR(falseLabels.peek()));
                    break;
                case RETURN:
                    addIR(new UnaryIR(op, null));
                    break;
                case FUNCTION:
                    if ("void".equals(((Function)root.value).retType))
                        addIR(new UnaryIR(OP.CALL, root.value));
                    else {
                        tmp = newTmp();
                        addIR(new BinaryIR(OP.CALL, tmp, root.value));
                        return tmp;
                    }
                    break;
                case VAR_OFFSET:
                    if (((OffsetVar)root.value).variable.isCollapsible()) break;
                    return root.value;
                case IMMEDIATE:
                    return root.value;
                case VARIABLE:
                    if (((Variable)root.value).isCollapsible()) break;
                    return root.value;
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
                addIR(new TernaryIR(op, tmp, left, right));
                return tmp;
            case MINUS:
            case NEGATE:
                tmp = newTmp();
                AstValue sub = parseAst(root.getLeft());
                addIR(new BinaryIR(op, tmp, sub));
                return tmp;
            case ASSIGN:
                left = parseAst(root.getLeft());
                right = parseAst(root.getRight());
                addIR(new BinaryIR(op, left, right));
                break;
            case STATEMENT:
            case ROOT:
            case ASSIGN_GROUP:
                for (AstNode child : root.getSubTrees()) parseAst(child);
                break;

            case CALL:
                parseAst(root.getLeft());
                parseAst(root.getRight());
                break;
            case PARAM:
                for (AstNode child : root.getSubTrees()) {
                    AstValue res = parseAst(child);
                    addIR(new UnaryIR(op, res));
                }
                break;
            case IF_ELSE:
                cond = parseAst(root.getLeft());
                trueLabel = Label.newLabel();
                falseLabel = Label.newLabel();
                trueLabels.push(trueLabel);
                falseLabels.push(falseLabel);
                addIR(new CondGoToIR(trueLabel, cond));
                parseAst(root.getNode(2));  // false block
                addIR(new GoToIR(falseLabel));
                setNextLabel(trueLabel);
                parseAst(root.getNode(1));  // true block
                setNextLabel(falseLabel);
                trueLabels.pop();
                falseLabels.pop();
                break;
            case WHILE:
                cond = parseAst(root.getLeft());
                trueLabel = Label.newLabel();
                falseLabel = Label.newLabel();
                trueLabels.push(trueLabel);
                falseLabels.push(falseLabel);
                IR ir = addIR(new CondGoToIR(trueLabel, cond));
                addIR(new GoToIR(falseLabel));
                setNextLabel(trueLabel);
                parseAst(root.getRight());
                addIR(new GoToIR(Label.newLabel(ir)));
                setNextLabel(falseLabel);
                falseLabels.pop();
                trueLabels.pop();
                break;
            case RETURN:
                left = parseAst(root.getLeft());
                addIR(new UnaryIR(OP.RETURN, left));
                break;
            case CONST_DECL:
            case VAR_DECL:
                return parseAst(root.getLeft());
            case FUNC_DECL:
                setNextLabel(Label.newLabel(root.getLeft().value.getVal()));
                parseAst(root.getRight());
                break;
        }
        return null;

    }
}
