// Generated from D:/java/SysYCompiler/src\SysY.g4 by ANTLR 4.9.1

package antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SysYParser}.
 */
public interface SysYListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SysYParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(SysYParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link SysYParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(SysYParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code printExpr}
	 * labeled alternative in {@link SysYParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterPrintExpr(SysYParser.PrintExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code printExpr}
	 * labeled alternative in {@link SysYParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitPrintExpr(SysYParser.PrintExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assign}
	 * labeled alternative in {@link SysYParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssign(SysYParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assign}
	 * labeled alternative in {@link SysYParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssign(SysYParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blank}
	 * labeled alternative in {@link SysYParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBlank(SysYParser.BlankContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blank}
	 * labeled alternative in {@link SysYParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBlank(SysYParser.BlankContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parens}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(SysYParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(SysYParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(SysYParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(SysYParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(SysYParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(SysYParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterId(SysYParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitId(SysYParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code int}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInt(SysYParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code int}
	 * labeled alternative in {@link SysYParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInt(SysYParser.IntContext ctx);
}