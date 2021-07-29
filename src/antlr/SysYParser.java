// Generated from D:/java/SysYCompiler/src\SysY.g4 by ANTLR 4.9.1

package antlr;

import compiler.genir.IRCollection;
import compiler.genir.IRFunction;
import compiler.genir.code.AddressOrData;
import compiler.genir.code.GotoRepresent;
import compiler.genir.code.InterRepresentHolder;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import compiler.symboltable.SymbolDomain;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SysYParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		Identifier=10, Integer_const=11, LeftParen=12, RightParen=13, LeftBracket=14, 
		RightBracket=15, LeftBrace=16, RightBrace=17, Less=18, LessEqual=19, Greater=20, 
		GreaterEqual=21, Plus=22, Minus=23, Star=24, Div=25, Mod=26, And=27, Or=28, 
		Not=29, Colon=30, Semi=31, Comma=32, Assign=33, Equal=34, NotEqual=35, 
		Whitespace=36, Newline=37, BlockComment=38, LineComment=39;
	public static final int
		RULE_compUnit = 0, RULE_decl = 1, RULE_constDecl = 2, RULE_bType = 3, 
		RULE_constDef = 4, RULE_constInitVal = 5, RULE_varDecl = 6, RULE_varDef = 7, 
		RULE_initVal = 8, RULE_funcDef = 9, RULE_funcType = 10, RULE_funcFParams = 11, 
		RULE_funcFParam = 12, RULE_block = 13, RULE_blockItem = 14, RULE_stmt = 15, 
		RULE_exp = 16, RULE_cond = 17, RULE_lVal = 18, RULE_primaryExp = 19, RULE_unaryExp = 20, 
		RULE_funcRParams = 21, RULE_mulExp = 22, RULE_addExp = 23, RULE_relExp = 24, 
		RULE_eqExp = 25, RULE_lAndExp = 26, RULE_lOrExp = 27, RULE_constExp = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"compUnit", "decl", "constDecl", "bType", "constDef", "constInitVal", 
			"varDecl", "varDef", "initVal", "funcDef", "funcType", "funcFParams", 
			"funcFParam", "block", "blockItem", "stmt", "exp", "cond", "lVal", "primaryExp", 
			"unaryExp", "funcRParams", "mulExp", "addExp", "relExp", "eqExp", "lAndExp", 
			"lOrExp", "constExp"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'const'", "'int'", "'void'", "'if'", "'else'", "'while'", "'break'", 
			"'continue'", "'return'", null, null, "'('", "')'", "'['", "']'", "'{'", 
			"'}'", "'<'", "'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'/'", "'%'", 
			"'&&'", "'||'", "'!'", "':'", "';'", "','", "'='", "'=='", "'!='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "Identifier", 
			"Integer_const", "LeftParen", "RightParen", "LeftBracket", "RightBracket", 
			"LeftBrace", "RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", 
			"Plus", "Minus", "Star", "Div", "Mod", "And", "Or", "Not", "Colon", "Semi", 
			"Comma", "Assign", "Equal", "NotEqual", "Whitespace", "Newline", "BlockComment", 
			"LineComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SysY.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SysYParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class DomainedContext extends ParserRuleContext{
		public SymbolDomain domain;
		public DomainedContext() {
		}

		public DomainedContext(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}
	}

	public static class HasInterRepresentBase extends DomainedContext{
		public IRCollection irGroup;

		public HasInterRepresentBase(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}

		public HasInterRepresentBase() {
		}
	}

	public static interface IPositionable {
		public InterRepresentHolder getStartStmt();
		public InterRepresentHolder getEndStmt();

		public void setStartStmt( InterRepresentHolder irGroup);
		public void setEndStmt(InterRepresentHolder irGroup);
	}

	public static class PositionalbleBase extends DomainedContext implements  IPositionable{
		public PositionalbleBase() {
		}

		public PositionalbleBase(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}

		private InterRepresentHolder startStmt;
		private InterRepresentHolder endStmt;
		@Override
		public InterRepresentHolder getStartStmt() {
			return startStmt;
		}

		@Override
		public InterRepresentHolder getEndStmt() {
			return endStmt;
		}

		@Override
		public void setStartStmt(InterRepresentHolder irGroup) {
			startStmt = irGroup;
		}

		@Override
		public void setEndStmt(InterRepresentHolder irGroup) {
			endStmt = irGroup;
		}
	}

	public static class InitValContextBase extends DomainedContext{
		public int[] initValues;
		public int symbolOffset;
		public int whichDim; //第几个维度
		public int[] dimensions;
		public Token ident;
		public boolean hasConstInitValue = false;
		public InitValContextBase() {
		}

		public InitValContextBase(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}
	}
	public static class CompUnitContext extends DomainedContext {
		public List<DeclContext> decl() {
			return getRuleContexts(DeclContext.class);
		}
		public DeclContext decl(int i) {
			return getRuleContext(DeclContext.class,i);
		}
		public List<FuncDefContext> funcDef() {
			return getRuleContexts(FuncDefContext.class);
		}
		public FuncDefContext funcDef(int i) {
			return getRuleContext(FuncDefContext.class,i);
		}
		public CompUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterCompUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitCompUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitCompUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompUnitContext compUnit() throws RecognitionException {
		CompUnitContext _localctx = new CompUnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(60);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(58);
					decl();
					}
					break;
				case 2:
					{
					setState(59);
					funcDef();
					}
					break;
				}
				}
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclContext extends PositionalbleBase {
		public ConstDeclContext constDecl() {
			return getRuleContext(ConstDeclContext.class,0);
		}
		public VarDeclContext varDecl() {
			return getRuleContext(VarDeclContext.class,0);
		}
		public DeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclContext decl() throws RecognitionException {
		DeclContext _localctx = new DeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_decl);
		try {
			setState(66);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(64);
				constDecl();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(65);
				varDecl();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstDeclContext extends DomainedContext {
		public BTypeContext bType() {
			return getRuleContext(BTypeContext.class,0);
		}
		public List<ConstDefContext> constDef() {
			return getRuleContexts(ConstDefContext.class);
		}
		public ConstDefContext constDef(int i) {
			return getRuleContext(ConstDefContext.class,i);
		}
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public List<TerminalNode> Comma() { return getTokens(SysYParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SysYParser.Comma, i);
		}
		public ConstDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterConstDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitConstDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitConstDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstDeclContext constDecl() throws RecognitionException {
		ConstDeclContext _localctx = new ConstDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_constDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(T__0);
			setState(69);
			bType();
			setState(70);
			constDef();
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(71);
				match(Comma);
				setState(72);
				constDef();
				}
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(78);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BTypeContext extends DomainedContext {
		public BTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterBType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitBType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitBType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BTypeContext bType() throws RecognitionException {
		BTypeContext _localctx = new BTypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_bType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstDefContext extends DomainedContext {
		public TerminalNode Identifier() { return getToken(SysYParser.Identifier, 0); }
		public TerminalNode Assign() { return getToken(SysYParser.Assign, 0); }
		public ConstInitValContext constInitVal() {
			return getRuleContext(ConstInitValContext.class,0);
		}
		public List<TerminalNode> LeftBracket() { return getTokens(SysYParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(SysYParser.LeftBracket, i);
		}
		public List<ConstExpContext> constExp() {
			return getRuleContexts(ConstExpContext.class);
		}
		public ConstExpContext constExp(int i) {
			return getRuleContext(ConstExpContext.class,i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(SysYParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(SysYParser.RightBracket, i);
		}
		public ConstDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterConstDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitConstDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitConstDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstDefContext constDef() throws RecognitionException {
		ConstDefContext _localctx = new ConstDefContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_constDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(Identifier);
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LeftBracket) {
				{
				{
				setState(83);
				match(LeftBracket);
				setState(84);
				constExp();
				setState(85);
				match(RightBracket);
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(92);
			match(Assign);
			setState(93);
			constInitVal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstInitValContext extends InitValContextBase {

		public ConstExpContext constExp() {
			return getRuleContext(ConstExpContext.class,0);
		}
		public TerminalNode LeftBrace() { return getToken(SysYParser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(SysYParser.RightBrace, 0); }
		public List<ConstInitValContext> constInitVal() {
			return getRuleContexts(ConstInitValContext.class);
		}
		public ConstInitValContext constInitVal(int i) {
			return getRuleContext(ConstInitValContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(SysYParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SysYParser.Comma, i);
		}
		public ConstInitValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constInitVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterConstInitVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitConstInitVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitConstInitVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstInitValContext constInitVal() throws RecognitionException {
		ConstInitValContext _localctx = new ConstInitValContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_constInitVal);
		int _la;
		try {
			setState(108);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
			case Integer_const:
			case LeftParen:
			case Plus:
			case Minus:
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
				constExp();
				}
				break;
			case LeftBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				match(LeftBrace);
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Identifier) | (1L << Integer_const) | (1L << LeftParen) | (1L << LeftBrace) | (1L << Plus) | (1L << Minus) | (1L << Not))) != 0)) {
					{
					setState(97);
					constInitVal();
					setState(102);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==Comma) {
						{
						{
						setState(98);
						match(Comma);
						setState(99);
						constInitVal();
						}
						}
						setState(104);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(107);
				match(RightBrace);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclContext extends PositionalbleBase {
		public BTypeContext bType() {
			return getRuleContext(BTypeContext.class,0);
		}
		public List<VarDefContext> varDef() {
			return getRuleContexts(VarDefContext.class);
		}
		public VarDefContext varDef(int i) {
			return getRuleContext(VarDefContext.class,i);
		}
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public List<TerminalNode> Comma() { return getTokens(SysYParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SysYParser.Comma, i);
		}
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterVarDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitVarDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			bType();
			setState(111);
			varDef();
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(112);
				match(Comma);
				setState(113);
				varDef();
				}
				}
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(119);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDefContext extends PositionalbleBase {
		public TerminalNode Identifier() { return getToken(SysYParser.Identifier, 0); }
		public List<TerminalNode> LeftBracket() { return getTokens(SysYParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(SysYParser.LeftBracket, i);
		}
		public List<ConstExpContext> constExp() {
			return getRuleContexts(ConstExpContext.class);
		}
		public ConstExpContext constExp(int i) {
			return getRuleContext(ConstExpContext.class,i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(SysYParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(SysYParser.RightBracket, i);
		}
		public TerminalNode Assign() { return getToken(SysYParser.Assign, 0); }
		public InitValContext initVal() {
			return getRuleContext(InitValContext.class,0);
		}
		public VarDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterVarDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitVarDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitVarDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefContext varDef() throws RecognitionException {
		VarDefContext _localctx = new VarDefContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_varDef);
		int _la;
		try {
			setState(143);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(121);
				match(Identifier);
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==LeftBracket) {
					{
					{
					setState(122);
					match(LeftBracket);
					setState(123);
					constExp();
					setState(124);
					match(RightBracket);
					}
					}
					setState(130);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(131);
				match(Identifier);
				setState(138);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==LeftBracket) {
					{
					{
					setState(132);
					match(LeftBracket);
					setState(133);
					constExp();
					setState(134);
					match(RightBracket);
					}
					}
					setState(140);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(141);
				match(Assign);
				setState(142);
				initVal();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InitValContext extends InitValContextBase {

		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode LeftBrace() { return getToken(SysYParser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(SysYParser.RightBrace, 0); }
		public List<InitValContext> initVal() {
			return getRuleContexts(InitValContext.class);
		}
		public InitValContext initVal(int i) {
			return getRuleContext(InitValContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(SysYParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SysYParser.Comma, i);
		}
		public InitValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterInitVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitInitVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitInitVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitValContext initVal() throws RecognitionException {
		InitValContext _localctx = new InitValContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_initVal);
		int _la;
		try {
			setState(158);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
			case Integer_const:
			case LeftParen:
			case Plus:
			case Minus:
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(145);
				exp();
				}
				break;
			case LeftBrace:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
				match(LeftBrace);
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Identifier) | (1L << Integer_const) | (1L << LeftParen) | (1L << LeftBrace) | (1L << Plus) | (1L << Minus) | (1L << Not))) != 0)) {
					{
					setState(147);
					initVal();
					setState(152);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==Comma) {
						{
						{
						setState(148);
						match(Comma);
						setState(149);
						initVal();
						}
						}
						setState(154);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(157);
				match(RightBrace);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncDefContext extends DomainedContext {
		public IRFunction irFunction;
		public FuncTypeContext funcType() {
			return getRuleContext(FuncTypeContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(SysYParser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(SysYParser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(SysYParser.RightParen, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FuncFParamsContext funcFParams() {
			return getRuleContext(FuncFParamsContext.class,0);
		}
		public FuncDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterFuncDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitFuncDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitFuncDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncDefContext funcDef() throws RecognitionException {
		FuncDefContext _localctx = new FuncDefContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_funcDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			funcType();
			setState(161);
			match(Identifier);
			setState(162);
			match(LeftParen);
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(163);
				funcFParams();
				}
			}

			setState(166);
			match(RightParen);
			setState(167);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncTypeContext extends DomainedContext {
		public FuncTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterFuncType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitFuncType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitFuncType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncTypeContext funcType() throws RecognitionException {
		FuncTypeContext _localctx = new FuncTypeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_funcType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			_la = _input.LA(1);
			if ( !(_la==T__1 || _la==T__2) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncFParamsContext extends DomainedContext {
		public List<FuncFParamContext> funcFParam() {
			return getRuleContexts(FuncFParamContext.class);
		}
		public FuncFParamContext funcFParam(int i) {
			return getRuleContext(FuncFParamContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(SysYParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SysYParser.Comma, i);
		}
		public FuncFParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcFParams; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterFuncFParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitFuncFParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitFuncFParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncFParamsContext funcFParams() throws RecognitionException {
		FuncFParamsContext _localctx = new FuncFParamsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_funcFParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			funcFParam();
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(172);
				match(Comma);
				setState(173);
				funcFParam();
				}
				}
				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncFParamContext extends DomainedContext {
		public BTypeContext bType() {
			return getRuleContext(BTypeContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(SysYParser.Identifier, 0); }
		public List<TerminalNode> LeftBracket() { return getTokens(SysYParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(SysYParser.LeftBracket, i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(SysYParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(SysYParser.RightBracket, i);
		}
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public FuncFParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcFParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterFuncFParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitFuncFParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitFuncFParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncFParamContext funcFParam() throws RecognitionException {
		FuncFParamContext _localctx = new FuncFParamContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_funcFParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			bType();
			setState(180);
			match(Identifier);
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LeftBracket) {
				{
				setState(181);
				match(LeftBracket);
				setState(182);
				match(RightBracket);
				setState(189);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==LeftBracket) {
					{
					{
					setState(183);
					match(LeftBracket);
					setState(184);
					exp();
					setState(185);
					match(RightBracket);
					}
					}
					setState(191);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends StmtContext implements IHasBreakOrContinue {
		public TerminalNode LeftBrace() { return getToken(SysYParser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(SysYParser.RightBrace, 0); }
		public List<BlockItemContext> blockItem() {
			return getRuleContexts(BlockItemContext.class);
		}
		public BlockItemContext blockItem(int i) {
			return getRuleContext(BlockItemContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}

		private List<InterRepresentHolder> breakQuads = null;
		private List<InterRepresentHolder> continueQuads= null;
		@Override
		public List<InterRepresentHolder> getBreakQuads() {
			return breakQuads;
		}

		@Override
		public List<InterRepresentHolder> getContinueQuads() {
			return continueQuads;
		}

		@Override
		public void setBreakQuads(List<InterRepresentHolder> quads) {
			this.breakQuads=quads;
		}

		@Override
		public void setContinueQuads(List<InterRepresentHolder> quads) {
			this.continueQuads=quads;
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(LeftBrace);
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__3) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << Identifier) | (1L << Integer_const) | (1L << LeftParen) | (1L << LeftBrace) | (1L << Plus) | (1L << Minus) | (1L << Not) | (1L << Semi))) != 0)) {
				{
				{
				setState(195);
				blockItem();
				}
				}
				setState(200);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(201);
			match(RightBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockItemContext extends PositionalbleBase implements IHasBreakOrContinue{
		public DeclContext decl() {
			return getRuleContext(DeclContext.class,0);
		}
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public BlockItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterBlockItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitBlockItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitBlockItem(this);
			else return visitor.visitChildren(this);
		}
		private List<InterRepresentHolder> breakQuads=null;
		private List<InterRepresentHolder> continueQuads=null;
		@Override
		public List<InterRepresentHolder> getBreakQuads() {
			return breakQuads;
		}

		@Override
		public List<InterRepresentHolder> getContinueQuads() {
			return continueQuads;
		}

		@Override
		public void setBreakQuads(List<InterRepresentHolder> quads) {
			breakQuads=quads;
		}

		@Override
		public void setContinueQuads(List<InterRepresentHolder> quads) {
			continueQuads=quads;
		}


	}

	public final BlockItemContext blockItem() throws RecognitionException {
		BlockItemContext _localctx = new BlockItemContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_blockItem);
		try {
			setState(205);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
				enterOuterAlt(_localctx, 1);
				{
				setState(203);
				decl();
				}
				break;
			case T__3:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case Identifier:
			case Integer_const:
			case LeftParen:
			case LeftBrace:
			case Plus:
			case Minus:
			case Not:
			case Semi:
				enterOuterAlt(_localctx, 2);
				{
				setState(204);
				stmt();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}
	public static interface IHasBreakOrContinue
	{
		List<InterRepresentHolder> getBreakQuads();
		List<InterRepresentHolder> getContinueQuads();
		void setBreakQuads(List<InterRepresentHolder> quads);
		void setContinueQuads(List<InterRepresentHolder> quads);
	}
	public static class StmtContext extends PositionalbleBase implements IHasBreakOrContinue {

		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }

		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}

		private List<InterRepresentHolder> breakQuads = null;
		private List<InterRepresentHolder> continueQuads= null;
		@Override
		public List<InterRepresentHolder> getBreakQuads() {
			return breakQuads;
		}

		@Override
		public List<InterRepresentHolder> getContinueQuads() {
			return continueQuads;
		}

		@Override
		public void setBreakQuads(List<InterRepresentHolder> quads) {
			this.breakQuads=quads;
		}

		@Override
		public void setContinueQuads(List<InterRepresentHolder> quads) {
			this.continueQuads=quads;
		}

	}
	public static class IfStatContext extends StmtContext {
		public TerminalNode LeftParen() { return getToken(SysYParser.LeftParen, 0); }
		public CondContext cond() {
			return getRuleContext(CondContext.class,0);
		}
		public TerminalNode RightParen() { return getToken(SysYParser.RightParen, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public IfStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterIfStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitIfStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitIfStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BlockStatContext extends StmtContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public BlockStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterBlockStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitBlockStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitBlockStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReturnStatContext extends StmtContext {
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public ReturnStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterReturnStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitReturnStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitReturnStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContinueStatContext extends StmtContext {
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public ContinueStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterContinueStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitContinueStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitContinueStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SemiStatContext extends StmtContext {
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public SemiStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterSemiStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitSemiStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitSemiStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignStatContext extends StmtContext {
		public LValContext lVal() {
			return getRuleContext(LValContext.class,0);
		}
		public TerminalNode Assign() { return getToken(SysYParser.Assign, 0); }
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public AssignStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterAssignStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitAssignStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitAssignStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BreakStatContext extends StmtContext {
		public TerminalNode Semi() { return getToken(SysYParser.Semi, 0); }
		public BreakStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterBreakStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitBreakStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitBreakStat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhileStatContext extends StmtContext {
		public TerminalNode LeftParen() { return getToken(SysYParser.LeftParen, 0); }
		public CondContext cond() {
			return getRuleContext(CondContext.class,0);
		}
		public TerminalNode RightParen() { return getToken(SysYParser.RightParen, 0); }
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public WhileStatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterWhileStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitWhileStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitWhileStat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_stmt);
		int _la;
		try {
			setState(241);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new AssignStatContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(207);
				lVal();
				setState(208);
				match(Assign);
				setState(209);
				exp();
				setState(210);
				match(Semi);
				}
				break;
			case 2:
				_localctx = new SemiStatContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Identifier) | (1L << Integer_const) | (1L << LeftParen) | (1L << Plus) | (1L << Minus) | (1L << Not))) != 0)) {
					{
					setState(212);
					exp();
					}
				}

				setState(215);
				match(Semi);
				}
				break;
			case 3:
				_localctx = new BlockStatContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(216);
				block();
				}
				break;
			case 4:
				_localctx = new IfStatContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(217);
				match(T__3);
				setState(218);
				match(LeftParen);
				setState(219);
				cond();
				setState(220);
				match(RightParen);
				setState(221);
				stmt();
				setState(224);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(222);
					match(T__4);
					setState(223);
					stmt();
					}
					break;
				}
				}
				break;
			case 5:
				_localctx = new WhileStatContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(226);
				match(T__5);
				setState(227);
				match(LeftParen);
				setState(228);
				cond();
				setState(229);
				match(RightParen);
				setState(230);
				stmt();
				}
				break;
			case 6:
				_localctx = new BreakStatContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(232);
				match(T__6);
				setState(233);
				match(Semi);
				}
				break;
			case 7:
				_localctx = new ContinueStatContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(234);
				match(T__7);
				setState(235);
				match(Semi);
				}
				break;
			case 8:
				_localctx = new ReturnStatContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(236);
				match(T__8);
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Identifier) | (1L << Integer_const) | (1L << LeftParen) | (1L << Plus) | (1L << Minus) | (1L << Not))) != 0)) {
					{
					setState(237);
					exp();
					}
				}

				setState(240);
				match(Semi);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpContextBase extends DomainedContext{
		public AddressOrData result;
		public InterRepresentHolder startStmt;
		public boolean not;
		public ExpContextBase() {
		}

		public ExpContextBase(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}

		/*public IRGroup startStmt;
		public IRGroup endStmt;
		@Override
		public IRGroup getStartStmt() {
			return startStmt;
		}

		@Override
		public IRGroup getEndStmt() {
			return endStmt;
		}

		@Override
		public void setStartStmt(IRGroup irGroup) {
			startStmt = irGroup;
		}

		@Override
		public void setEndStmt(IRGroup irGroup) {
			endStmt = irGroup;
		}*/
	}

	public static class ExpContext extends ExpContextBase {
		public AddExpContext addExp() {
			return getRuleContext(AddExpContext.class,0);
		}
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpContext exp() throws RecognitionException {
		ExpContext _localctx = new ExpContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_exp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			addExp(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CondContext extends BranchContextBase {
		public LOrExpContext lOrExp() {
			return getRuleContext(LOrExpContext.class,0);
		}
		public CondContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterCond(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitCond(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitCond(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CondContext cond() throws RecognitionException {
		CondContext _localctx = new CondContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_cond);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			lOrExp(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LValContext extends DomainedContext {
		public InterRepresentHolder startStmt;
		public TerminalNode Identifier() { return getToken(SysYParser.Identifier, 0); }
		public List<TerminalNode> LeftBracket() { return getTokens(SysYParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(SysYParser.LeftBracket, i);
		}
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(SysYParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(SysYParser.RightBracket, i);
		}
		public LValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterLVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitLVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitLVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LValContext lVal() throws RecognitionException {
		LValContext _localctx = new LValContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_lVal);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			match(Identifier);
			setState(254);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(248);
					match(LeftBracket);
					setState(249);
					exp();
					setState(250);
					match(RightBracket);
					}
					} 
				}
				setState(256);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryExpContext extends ExpContextBase {
		public TerminalNode LeftParen() { return getToken(SysYParser.LeftParen, 0); }
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode RightParen() { return getToken(SysYParser.RightParen, 0); }
		public LValContext lVal() {
			return getRuleContext(LValContext.class,0);
		}
		public TerminalNode Integer_const() { return getToken(SysYParser.Integer_const, 0); }
		public PrimaryExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterPrimaryExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitPrimaryExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitPrimaryExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExpContext primaryExp() throws RecognitionException {
		PrimaryExpContext _localctx = new PrimaryExpContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_primaryExp);
		try {
			setState(263);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LeftParen:
				enterOuterAlt(_localctx, 1);
				{
				setState(257);
				match(LeftParen);
				setState(258);
				exp();
				setState(259);
				match(RightParen);
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(261);
				lVal();
				}
				break;
			case Integer_const:
				enterOuterAlt(_localctx, 3);
				{
				setState(262);
				match(Integer_const);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryExpContext extends ExpContextBase {
		public UnaryExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExp; }
	 
		public UnaryExpContext() { }
		public void copyFrom(UnaryExpContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrimaryExprContext extends UnaryExpContext {
		public PrimaryExpContext primaryExp() {
			return getRuleContext(PrimaryExpContext.class,0);
		}
		public PrimaryExprContext(UnaryExpContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterPrimaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitPrimaryExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitPrimaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SignExprContext extends UnaryExpContext {
		public Token op;
		public UnaryExpContext unaryExp() {
			return getRuleContext(UnaryExpContext.class,0);
		}
		public TerminalNode Plus() { return getToken(SysYParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(SysYParser.Minus, 0); }
		public TerminalNode Not() { return getToken(SysYParser.Not, 0); }
		public SignExprContext(UnaryExpContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterSignExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitSignExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitSignExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionExprContext extends UnaryExpContext {
		public TerminalNode Identifier() { return getToken(SysYParser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(SysYParser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(SysYParser.RightParen, 0); }
		public FuncRParamsContext funcRParams() {
			return getRuleContext(FuncRParamsContext.class,0);
		}
		public FunctionExprContext(UnaryExpContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterFunctionExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitFunctionExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitFunctionExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpContext unaryExp() throws RecognitionException {
		UnaryExpContext _localctx = new UnaryExpContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_unaryExp);
		int _la;
		try {
			setState(274);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				_localctx = new PrimaryExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(265);
				primaryExp();
				}
				break;
			case 2:
				_localctx = new FunctionExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(266);
				match(Identifier);
				setState(267);
				match(LeftParen);
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Identifier) | (1L << Integer_const) | (1L << LeftParen) | (1L << Plus) | (1L << Minus) | (1L << Not))) != 0)) {
					{
					setState(268);
					funcRParams();
					}
				}

				setState(271);
				match(RightParen);
				}
				break;
			case 3:
				_localctx = new SignExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(272);
				((SignExprContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Plus) | (1L << Minus) | (1L << Not))) != 0)) ) {
					((SignExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(273);
				unaryExp();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncRParamsContext extends DomainedContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(SysYParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SysYParser.Comma, i);
		}
		public FuncRParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcRParams; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterFuncRParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitFuncRParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitFuncRParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncRParamsContext funcRParams() throws RecognitionException {
		FuncRParamsContext _localctx = new FuncRParamsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_funcRParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(276);
			exp();
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(277);
				match(Comma);
				setState(278);
				exp();
				}
				}
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MulExpContext extends ExpContextBase {
		public Token op;
		public UnaryExpContext unaryExp() {
			return getRuleContext(UnaryExpContext.class,0);
		}
		public MulExpContext mulExp() {
			return getRuleContext(MulExpContext.class,0);
		}
		public TerminalNode Star() { return getToken(SysYParser.Star, 0); }
		public TerminalNode Div() { return getToken(SysYParser.Div, 0); }
		public TerminalNode Mod() { return getToken(SysYParser.Mod, 0); }
		public MulExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mulExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterMulExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitMulExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitMulExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MulExpContext mulExp() throws RecognitionException {
		return mulExp(0);
	}

	private MulExpContext mulExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MulExpContext _localctx = new MulExpContext(_ctx, _parentState);
		MulExpContext _prevctx = _localctx;
		int _startState = 44;
		enterRecursionRule(_localctx, 44, RULE_mulExp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(285);
			unaryExp();
			}
			_ctx.stop = _input.LT(-1);
			setState(292);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MulExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_mulExp);
					setState(287);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(288);
					((MulExpContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Star) | (1L << Div) | (1L << Mod))) != 0)) ) {
						((MulExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(289);
					unaryExp();
					}
					} 
				}
				setState(294);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class AddExpContext extends ExpContextBase {
		public Token op;
		public MulExpContext mulExp() {
			return getRuleContext(MulExpContext.class,0);
		}
		public AddExpContext addExp() {
			return getRuleContext(AddExpContext.class,0);
		}
		public TerminalNode Plus() { return getToken(SysYParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(SysYParser.Minus, 0); }
		public AddExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterAddExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitAddExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitAddExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddExpContext addExp() throws RecognitionException {
		return addExp(0);
	}

	private AddExpContext addExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AddExpContext _localctx = new AddExpContext(_ctx, _parentState);
		AddExpContext _prevctx = _localctx;
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_addExp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(296);
			mulExp(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(303);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AddExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_addExp);
					setState(298);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(299);
					((AddExpContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==Plus || _la==Minus) ) {
						((AddExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(300);
					mulExp(0);
					}
					} 
				}
				setState(305);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}
	public static class BranchContextBase extends DomainedContext {
		public BranchContextBase(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}
		public List<GotoRepresent> trueList = null;
		public List<GotoRepresent> falseList = null;
		public InterRepresentHolder startStmt;
	}
	public static class RelExpContextBase extends BranchContextBase{
		public RelExpContextBase(ParserRuleContext parent, int invokingStateNumber) {
			super(parent, invokingStateNumber);
		}
		public AddressOrData address;
		//public InterRepresentHolder vocancy;
	}

	public static class RelExpContext extends RelExpContextBase {
		public Token op;
		public AddExpContext addExp() {
			return getRuleContext(AddExpContext.class,0);
		}
		public RelExpContext relExp() {
			return getRuleContext(RelExpContext.class,0);
		}
		public TerminalNode LessEqual() { return getToken(SysYParser.LessEqual, 0); }
		public TerminalNode GreaterEqual() { return getToken(SysYParser.GreaterEqual, 0); }
		public TerminalNode Less() { return getToken(SysYParser.Less, 0); }
		public TerminalNode Greater() { return getToken(SysYParser.Greater, 0); }
		public RelExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterRelExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitRelExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitRelExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelExpContext relExp() throws RecognitionException {
		return relExp(0);
	}

	private RelExpContext relExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RelExpContext _localctx = new RelExpContext(_ctx, _parentState);
		RelExpContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_relExp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(307);
			addExp(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(314);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new RelExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_relExp);
					setState(309);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(310);
					((RelExpContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Less) | (1L << LessEqual) | (1L << Greater) | (1L << GreaterEqual))) != 0)) ) {
						((RelExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(311);
					addExp(0);
					}
					} 
				}
				setState(316);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class EqExpContext extends RelExpContextBase {
		public Token op;
		public RelExpContext relExp() {
			return getRuleContext(RelExpContext.class,0);
		}
		public EqExpContext eqExp() {
			return getRuleContext(EqExpContext.class,0);
		}
		public TerminalNode Equal() { return getToken(SysYParser.Equal, 0); }
		public TerminalNode NotEqual() { return getToken(SysYParser.NotEqual, 0); }
		public EqExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eqExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterEqExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitEqExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitEqExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqExpContext eqExp() throws RecognitionException {
		return eqExp(0);
	}

	private EqExpContext eqExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		EqExpContext _localctx = new EqExpContext(_ctx, _parentState);
		EqExpContext _prevctx = _localctx;
		int _startState = 50;
		enterRecursionRule(_localctx, 50, RULE_eqExp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(318);
			relExp(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(325);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EqExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_eqExp);
					setState(320);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(321);
					((EqExpContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==Equal || _la==NotEqual) ) {
						((EqExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(322);
					relExp(0);
					}
					} 
				}
				setState(327);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class LAndExpContext extends RelExpContextBase {
		public EqExpContext eqExp() {
			return getRuleContext(EqExpContext.class,0);
		}
		public LAndExpContext lAndExp() {
			return getRuleContext(LAndExpContext.class,0);
		}
		public TerminalNode And() { return getToken(SysYParser.And, 0); }
		public LAndExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lAndExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterLAndExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitLAndExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitLAndExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LAndExpContext lAndExp() throws RecognitionException {
		return lAndExp(0);
	}

	private LAndExpContext lAndExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		LAndExpContext _localctx = new LAndExpContext(_ctx, _parentState);
		LAndExpContext _prevctx = _localctx;
		int _startState = 52;
		enterRecursionRule(_localctx, 52, RULE_lAndExp, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(329);
			eqExp(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(336);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new LAndExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_lAndExp);
					setState(331);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(332);
					match(And);
					setState(333);
					eqExp(0);
					}
					} 
				}
				setState(338);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class LOrExpContext extends RelExpContextBase {
		public LAndExpContext lAndExp() {
			return getRuleContext(LAndExpContext.class,0);
		}
		public LOrExpContext lOrExp() {
			return getRuleContext(LOrExpContext.class,0);
		}
		public TerminalNode Or() { return getToken(SysYParser.Or, 0); }
		public LOrExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lOrExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterLOrExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitLOrExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitLOrExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LOrExpContext lOrExp() throws RecognitionException {
		return lOrExp(0);
	}

	private LOrExpContext lOrExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		LOrExpContext _localctx = new LOrExpContext(_ctx, _parentState);
		LOrExpContext _prevctx = _localctx;
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_lOrExp, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(340);
			lAndExp(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(347);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new LOrExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_lOrExp);
					setState(342);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(343);
					match(Or);
					setState(344);
					lAndExp(0);
					}
					} 
				}
				setState(349);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ConstExpContext extends ExpContext {
		/*public AddExpContext addExp() {
			return getRuleContext(AddExpContext.class,0);
		}*/
		public ConstExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		/*@Override public int getRuleIndex() { return RULE_constExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).enterConstExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SysYListener ) ((SysYListener)listener).exitConstExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SysYVisitor ) return ((SysYVisitor<? extends T>)visitor).visitConstExp(this);
			else return visitor.visitChildren(this);
		}*/
	}

	public final ConstExpContext constExp() throws RecognitionException {
		ConstExpContext _localctx = new ConstExpContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_constExp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(350);
			addExp(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 22:
			return mulExp_sempred((MulExpContext)_localctx, predIndex);
		case 23:
			return addExp_sempred((AddExpContext)_localctx, predIndex);
		case 24:
			return relExp_sempred((RelExpContext)_localctx, predIndex);
		case 25:
			return eqExp_sempred((EqExpContext)_localctx, predIndex);
		case 26:
			return lAndExp_sempred((LAndExpContext)_localctx, predIndex);
		case 27:
			return lOrExp_sempred((LOrExpContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean mulExp_sempred(MulExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean addExp_sempred(AddExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean relExp_sempred(RelExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean eqExp_sempred(EqExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean lAndExp_sempred(LAndExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean lOrExp_sempred(LOrExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3)\u0163\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\6\2?\n\2\r"+
		"\2\16\2@\3\3\3\3\5\3E\n\3\3\4\3\4\3\4\3\4\3\4\7\4L\n\4\f\4\16\4O\13\4"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\7\6Z\n\6\f\6\16\6]\13\6\3\6\3\6\3"+
		"\6\3\7\3\7\3\7\3\7\3\7\7\7g\n\7\f\7\16\7j\13\7\5\7l\n\7\3\7\5\7o\n\7\3"+
		"\b\3\b\3\b\3\b\7\bu\n\b\f\b\16\bx\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\7\t"+
		"\u0081\n\t\f\t\16\t\u0084\13\t\3\t\3\t\3\t\3\t\3\t\7\t\u008b\n\t\f\t\16"+
		"\t\u008e\13\t\3\t\3\t\5\t\u0092\n\t\3\n\3\n\3\n\3\n\3\n\7\n\u0099\n\n"+
		"\f\n\16\n\u009c\13\n\5\n\u009e\n\n\3\n\5\n\u00a1\n\n\3\13\3\13\3\13\3"+
		"\13\5\13\u00a7\n\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\7\r\u00b1\n\r\f"+
		"\r\16\r\u00b4\13\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u00be"+
		"\n\16\f\16\16\16\u00c1\13\16\5\16\u00c3\n\16\3\17\3\17\7\17\u00c7\n\17"+
		"\f\17\16\17\u00ca\13\17\3\17\3\17\3\20\3\20\5\20\u00d0\n\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\5\21\u00d8\n\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\5\21\u00e3\n\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\5\21\u00f1\n\21\3\21\5\21\u00f4\n\21\3\22\3\22\3\23\3"+
		"\23\3\24\3\24\3\24\3\24\3\24\7\24\u00ff\n\24\f\24\16\24\u0102\13\24\3"+
		"\25\3\25\3\25\3\25\3\25\3\25\5\25\u010a\n\25\3\26\3\26\3\26\3\26\5\26"+
		"\u0110\n\26\3\26\3\26\3\26\5\26\u0115\n\26\3\27\3\27\3\27\7\27\u011a\n"+
		"\27\f\27\16\27\u011d\13\27\3\30\3\30\3\30\3\30\3\30\3\30\7\30\u0125\n"+
		"\30\f\30\16\30\u0128\13\30\3\31\3\31\3\31\3\31\3\31\3\31\7\31\u0130\n"+
		"\31\f\31\16\31\u0133\13\31\3\32\3\32\3\32\3\32\3\32\3\32\7\32\u013b\n"+
		"\32\f\32\16\32\u013e\13\32\3\33\3\33\3\33\3\33\3\33\3\33\7\33\u0146\n"+
		"\33\f\33\16\33\u0149\13\33\3\34\3\34\3\34\3\34\3\34\3\34\7\34\u0151\n"+
		"\34\f\34\16\34\u0154\13\34\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u015c\n"+
		"\35\f\35\16\35\u015f\13\35\3\36\3\36\3\36\2\b.\60\62\64\668\37\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:\2\b\3\2\4\5\4\2"+
		"\30\31\37\37\3\2\32\34\3\2\30\31\3\2\24\27\3\2$%\2\u0171\2>\3\2\2\2\4"+
		"D\3\2\2\2\6F\3\2\2\2\bR\3\2\2\2\nT\3\2\2\2\fn\3\2\2\2\16p\3\2\2\2\20\u0091"+
		"\3\2\2\2\22\u00a0\3\2\2\2\24\u00a2\3\2\2\2\26\u00ab\3\2\2\2\30\u00ad\3"+
		"\2\2\2\32\u00b5\3\2\2\2\34\u00c4\3\2\2\2\36\u00cf\3\2\2\2 \u00f3\3\2\2"+
		"\2\"\u00f5\3\2\2\2$\u00f7\3\2\2\2&\u00f9\3\2\2\2(\u0109\3\2\2\2*\u0114"+
		"\3\2\2\2,\u0116\3\2\2\2.\u011e\3\2\2\2\60\u0129\3\2\2\2\62\u0134\3\2\2"+
		"\2\64\u013f\3\2\2\2\66\u014a\3\2\2\28\u0155\3\2\2\2:\u0160\3\2\2\2<?\5"+
		"\4\3\2=?\5\24\13\2><\3\2\2\2>=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2A"+
		"\3\3\2\2\2BE\5\6\4\2CE\5\16\b\2DB\3\2\2\2DC\3\2\2\2E\5\3\2\2\2FG\7\3\2"+
		"\2GH\5\b\5\2HM\5\n\6\2IJ\7\"\2\2JL\5\n\6\2KI\3\2\2\2LO\3\2\2\2MK\3\2\2"+
		"\2MN\3\2\2\2NP\3\2\2\2OM\3\2\2\2PQ\7!\2\2Q\7\3\2\2\2RS\7\4\2\2S\t\3\2"+
		"\2\2T[\7\f\2\2UV\7\20\2\2VW\5:\36\2WX\7\21\2\2XZ\3\2\2\2YU\3\2\2\2Z]\3"+
		"\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2][\3\2\2\2^_\7#\2\2_`\5\f\7\2`\13"+
		"\3\2\2\2ao\5:\36\2bk\7\22\2\2ch\5\f\7\2de\7\"\2\2eg\5\f\7\2fd\3\2\2\2"+
		"gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2il\3\2\2\2jh\3\2\2\2kc\3\2\2\2kl\3\2\2\2"+
		"lm\3\2\2\2mo\7\23\2\2na\3\2\2\2nb\3\2\2\2o\r\3\2\2\2pq\5\b\5\2qv\5\20"+
		"\t\2rs\7\"\2\2su\5\20\t\2tr\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2wy\3"+
		"\2\2\2xv\3\2\2\2yz\7!\2\2z\17\3\2\2\2{\u0082\7\f\2\2|}\7\20\2\2}~\5:\36"+
		"\2~\177\7\21\2\2\177\u0081\3\2\2\2\u0080|\3\2\2\2\u0081\u0084\3\2\2\2"+
		"\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0092\3\2\2\2\u0084\u0082"+
		"\3\2\2\2\u0085\u008c\7\f\2\2\u0086\u0087\7\20\2\2\u0087\u0088\5:\36\2"+
		"\u0088\u0089\7\21\2\2\u0089\u008b\3\2\2\2\u008a\u0086\3\2\2\2\u008b\u008e"+
		"\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e"+
		"\u008c\3\2\2\2\u008f\u0090\7#\2\2\u0090\u0092\5\22\n\2\u0091{\3\2\2\2"+
		"\u0091\u0085\3\2\2\2\u0092\21\3\2\2\2\u0093\u00a1\5\"\22\2\u0094\u009d"+
		"\7\22\2\2\u0095\u009a\5\22\n\2\u0096\u0097\7\"\2\2\u0097\u0099\5\22\n"+
		"\2\u0098\u0096\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b"+
		"\3\2\2\2\u009b\u009e\3\2\2\2\u009c\u009a\3\2\2\2\u009d\u0095\3\2\2\2\u009d"+
		"\u009e\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a1\7\23\2\2\u00a0\u0093\3"+
		"\2\2\2\u00a0\u0094\3\2\2\2\u00a1\23\3\2\2\2\u00a2\u00a3\5\26\f\2\u00a3"+
		"\u00a4\7\f\2\2\u00a4\u00a6\7\16\2\2\u00a5\u00a7\5\30\r\2\u00a6\u00a5\3"+
		"\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\7\17\2\2\u00a9"+
		"\u00aa\5\34\17\2\u00aa\25\3\2\2\2\u00ab\u00ac\t\2\2\2\u00ac\27\3\2\2\2"+
		"\u00ad\u00b2\5\32\16\2\u00ae\u00af\7\"\2\2\u00af\u00b1\5\32\16\2\u00b0"+
		"\u00ae\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2"+
		"\2\2\u00b3\31\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00b6\5\b\5\2\u00b6\u00c2"+
		"\7\f\2\2\u00b7\u00b8\7\20\2\2\u00b8\u00bf\7\21\2\2\u00b9\u00ba\7\20\2"+
		"\2\u00ba\u00bb\5\"\22\2\u00bb\u00bc\7\21\2\2\u00bc\u00be\3\2\2\2\u00bd"+
		"\u00b9\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2"+
		"\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00b7\3\2\2\2\u00c2"+
		"\u00c3\3\2\2\2\u00c3\33\3\2\2\2\u00c4\u00c8\7\22\2\2\u00c5\u00c7\5\36"+
		"\20\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8"+
		"\u00c9\3\2\2\2\u00c9\u00cb\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cc\7\23"+
		"\2\2\u00cc\35\3\2\2\2\u00cd\u00d0\5\4\3\2\u00ce\u00d0\5 \21\2\u00cf\u00cd"+
		"\3\2\2\2\u00cf\u00ce\3\2\2\2\u00d0\37\3\2\2\2\u00d1\u00d2\5&\24\2\u00d2"+
		"\u00d3\7#\2\2\u00d3\u00d4\5\"\22\2\u00d4\u00d5\7!\2\2\u00d5\u00f4\3\2"+
		"\2\2\u00d6\u00d8\5\"\22\2\u00d7\u00d6\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8"+
		"\u00d9\3\2\2\2\u00d9\u00f4\7!\2\2\u00da\u00f4\5\34\17\2\u00db\u00dc\7"+
		"\6\2\2\u00dc\u00dd\7\16\2\2\u00dd\u00de\5$\23\2\u00de\u00df\7\17\2\2\u00df"+
		"\u00e2\5 \21\2\u00e0\u00e1\7\7\2\2\u00e1\u00e3\5 \21\2\u00e2\u00e0\3\2"+
		"\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00f4\3\2\2\2\u00e4\u00e5\7\b\2\2\u00e5"+
		"\u00e6\7\16\2\2\u00e6\u00e7\5$\23\2\u00e7\u00e8\7\17\2\2\u00e8\u00e9\5"+
		" \21\2\u00e9\u00f4\3\2\2\2\u00ea\u00eb\7\t\2\2\u00eb\u00f4\7!\2\2\u00ec"+
		"\u00ed\7\n\2\2\u00ed\u00f4\7!\2\2\u00ee\u00f0\7\13\2\2\u00ef\u00f1\5\""+
		"\22\2\u00f0\u00ef\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2"+
		"\u00f4\7!\2\2\u00f3\u00d1\3\2\2\2\u00f3\u00d7\3\2\2\2\u00f3\u00da\3\2"+
		"\2\2\u00f3\u00db\3\2\2\2\u00f3\u00e4\3\2\2\2\u00f3\u00ea\3\2\2\2\u00f3"+
		"\u00ec\3\2\2\2\u00f3\u00ee\3\2\2\2\u00f4!\3\2\2\2\u00f5\u00f6\5\60\31"+
		"\2\u00f6#\3\2\2\2\u00f7\u00f8\58\35\2\u00f8%\3\2\2\2\u00f9\u0100\7\f\2"+
		"\2\u00fa\u00fb\7\20\2\2\u00fb\u00fc\5\"\22\2\u00fc\u00fd\7\21\2\2\u00fd"+
		"\u00ff\3\2\2\2\u00fe\u00fa\3\2\2\2\u00ff\u0102\3\2\2\2\u0100\u00fe\3\2"+
		"\2\2\u0100\u0101\3\2\2\2\u0101\'\3\2\2\2\u0102\u0100\3\2\2\2\u0103\u0104"+
		"\7\16\2\2\u0104\u0105\5\"\22\2\u0105\u0106\7\17\2\2\u0106\u010a\3\2\2"+
		"\2\u0107\u010a\5&\24\2\u0108\u010a\7\r\2\2\u0109\u0103\3\2\2\2\u0109\u0107"+
		"\3\2\2\2\u0109\u0108\3\2\2\2\u010a)\3\2\2\2\u010b\u0115\5(\25\2\u010c"+
		"\u010d\7\f\2\2\u010d\u010f\7\16\2\2\u010e\u0110\5,\27\2\u010f\u010e\3"+
		"\2\2\2\u010f\u0110\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0115\7\17\2\2\u0112"+
		"\u0113\t\3\2\2\u0113\u0115\5*\26\2\u0114\u010b\3\2\2\2\u0114\u010c\3\2"+
		"\2\2\u0114\u0112\3\2\2\2\u0115+\3\2\2\2\u0116\u011b\5\"\22\2\u0117\u0118"+
		"\7\"\2\2\u0118\u011a\5\"\22\2\u0119\u0117\3\2\2\2\u011a\u011d\3\2\2\2"+
		"\u011b\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c-\3\2\2\2\u011d\u011b\3"+
		"\2\2\2\u011e\u011f\b\30\1\2\u011f\u0120\5*\26\2\u0120\u0126\3\2\2\2\u0121"+
		"\u0122\f\3\2\2\u0122\u0123\t\4\2\2\u0123\u0125\5*\26\2\u0124\u0121\3\2"+
		"\2\2\u0125\u0128\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127"+
		"/\3\2\2\2\u0128\u0126\3\2\2\2\u0129\u012a\b\31\1\2\u012a\u012b\5.\30\2"+
		"\u012b\u0131\3\2\2\2\u012c\u012d\f\3\2\2\u012d\u012e\t\5\2\2\u012e\u0130"+
		"\5.\30\2\u012f\u012c\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\61\3\2\2\2\u0133\u0131\3\2\2\2\u0134\u0135\b\32\1"+
		"\2\u0135\u0136\5\60\31\2\u0136\u013c\3\2\2\2\u0137\u0138\f\3\2\2\u0138"+
		"\u0139\t\6\2\2\u0139\u013b\5\60\31\2\u013a\u0137\3\2\2\2\u013b\u013e\3"+
		"\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d\63\3\2\2\2\u013e"+
		"\u013c\3\2\2\2\u013f\u0140\b\33\1\2\u0140\u0141\5\62\32\2\u0141\u0147"+
		"\3\2\2\2\u0142\u0143\f\3\2\2\u0143\u0144\t\7\2\2\u0144\u0146\5\62\32\2"+
		"\u0145\u0142\3\2\2\2\u0146\u0149\3\2\2\2\u0147\u0145\3\2\2\2\u0147\u0148"+
		"\3\2\2\2\u0148\65\3\2\2\2\u0149\u0147\3\2\2\2\u014a\u014b\b\34\1\2\u014b"+
		"\u014c\5\64\33\2\u014c\u0152\3\2\2\2\u014d\u014e\f\3\2\2\u014e\u014f\7"+
		"\35\2\2\u014f\u0151\5\64\33\2\u0150\u014d\3\2\2\2\u0151\u0154\3\2\2\2"+
		"\u0152\u0150\3\2\2\2\u0152\u0153\3\2\2\2\u0153\67\3\2\2\2\u0154\u0152"+
		"\3\2\2\2\u0155\u0156\b\35\1\2\u0156\u0157\5\66\34\2\u0157\u015d\3\2\2"+
		"\2\u0158\u0159\f\3\2\2\u0159\u015a\7\36\2\2\u015a\u015c\5\66\34\2\u015b"+
		"\u0158\3\2\2\2\u015c\u015f\3\2\2\2\u015d\u015b\3\2\2\2\u015d\u015e\3\2"+
		"\2\2\u015e9\3\2\2\2\u015f\u015d\3\2\2\2\u0160\u0161\5\60\31\2\u0161;\3"+
		"\2\2\2&>@DM[hknv\u0082\u008c\u0091\u009a\u009d\u00a0\u00a6\u00b2\u00bf"+
		"\u00c2\u00c8\u00cf\u00d7\u00e2\u00f0\u00f3\u0100\u0109\u010f\u0114\u011b"+
		"\u0126\u0131\u013c\u0147\u0152\u015d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}