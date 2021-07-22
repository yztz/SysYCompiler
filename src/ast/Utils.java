package ast;

public class Utils {
    public static void interpreterAst(AstNode root) {
        for (AstNode child : root.getSubTrees()) {
            interpreterAst(child);
        }
        System.out.println(root.value.getVal());

    }
}
