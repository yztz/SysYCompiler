// Generated from D:/javaFolder/SysYcompiler/src\SysY.g4 by ANTLR 4.9.1

package antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SysYLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"Identifier", "Decimal_const", "Octal_const", "Hexadecimal_const", "Integer_const", 
			"LeftParen", "RightParen", "LeftBracket", "RightBracket", "LeftBrace", 
			"RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", "Plus", 
			"Minus", "Star", "Div", "Mod", "And", "Or", "Not", "Colon", "Semi", "Comma", 
			"Assign", "Equal", "NotEqual", "Nondigit", "Digit", "NonZeroDigit", "OctalDigit", 
			"HexDigit", "HexPrefix", "Whitespace", "Newline", "BlockComment", "LineComment"
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


	public SysYLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SysY.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2)\u0123\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\3\2\3\2\3\2\3\2\3\2\3\2\3\3"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13\u009a\n\13\f"+
		"\13\16\13\u009d\13\13\3\f\3\f\7\f\u00a1\n\f\f\f\16\f\u00a4\13\f\3\r\3"+
		"\r\7\r\u00a8\n\r\f\r\16\r\u00ab\13\r\3\16\3\16\6\16\u00af\n\16\r\16\16"+
		"\16\u00b0\3\17\3\17\3\17\5\17\u00b6\n\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37"+
		"\3\37\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3&\3\'\3\'\3\'\3"+
		"(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3-\3.\6.\u00fc\n.\r.\16.\u00fd\3.\3"+
		".\3/\3/\5/\u0104\n/\3/\5/\u0107\n/\3/\3/\3\60\3\60\3\60\3\60\7\60\u010f"+
		"\n\60\f\60\16\60\u0112\13\60\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3"+
		"\61\7\61\u011d\n\61\f\61\16\61\u0120\13\61\3\61\3\61\3\u0110\2\62\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\2\31\2\33\2\35\r\37\16!"+
		"\17#\20%\21\'\22)\23+\24-\25/\26\61\27\63\30\65\31\67\329\33;\34=\35?"+
		"\36A\37C E!G\"I#K$M%O\2Q\2S\2U\2W\2Y\2[&]\'_(a)\3\2\n\5\2C\\aac|\3\2\62"+
		";\3\2\63;\3\2\629\5\2\62;CHch\4\2ZZzz\4\2\13\13\"\"\4\2\f\f\17\17\2\u0125"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\35\3\2\2\2"+
		"\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2"+
		"\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2"+
		"\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2"+
		"\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2["+
		"\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\3c\3\2\2\2\5i\3\2\2\2\7m\3\2"+
		"\2\2\tr\3\2\2\2\13u\3\2\2\2\rz\3\2\2\2\17\u0080\3\2\2\2\21\u0086\3\2\2"+
		"\2\23\u008f\3\2\2\2\25\u0096\3\2\2\2\27\u009e\3\2\2\2\31\u00a5\3\2\2\2"+
		"\33\u00ac\3\2\2\2\35\u00b5\3\2\2\2\37\u00b7\3\2\2\2!\u00b9\3\2\2\2#\u00bb"+
		"\3\2\2\2%\u00bd\3\2\2\2\'\u00bf\3\2\2\2)\u00c1\3\2\2\2+\u00c3\3\2\2\2"+
		"-\u00c5\3\2\2\2/\u00c8\3\2\2\2\61\u00ca\3\2\2\2\63\u00cd\3\2\2\2\65\u00cf"+
		"\3\2\2\2\67\u00d1\3\2\2\29\u00d3\3\2\2\2;\u00d5\3\2\2\2=\u00d7\3\2\2\2"+
		"?\u00da\3\2\2\2A\u00dd\3\2\2\2C\u00df\3\2\2\2E\u00e1\3\2\2\2G\u00e3\3"+
		"\2\2\2I\u00e5\3\2\2\2K\u00e7\3\2\2\2M\u00ea\3\2\2\2O\u00ed\3\2\2\2Q\u00ef"+
		"\3\2\2\2S\u00f1\3\2\2\2U\u00f3\3\2\2\2W\u00f5\3\2\2\2Y\u00f7\3\2\2\2["+
		"\u00fb\3\2\2\2]\u0106\3\2\2\2_\u010a\3\2\2\2a\u0118\3\2\2\2cd\7e\2\2d"+
		"e\7q\2\2ef\7p\2\2fg\7u\2\2gh\7v\2\2h\4\3\2\2\2ij\7k\2\2jk\7p\2\2kl\7v"+
		"\2\2l\6\3\2\2\2mn\7x\2\2no\7q\2\2op\7k\2\2pq\7f\2\2q\b\3\2\2\2rs\7k\2"+
		"\2st\7h\2\2t\n\3\2\2\2uv\7g\2\2vw\7n\2\2wx\7u\2\2xy\7g\2\2y\f\3\2\2\2"+
		"z{\7y\2\2{|\7j\2\2|}\7k\2\2}~\7n\2\2~\177\7g\2\2\177\16\3\2\2\2\u0080"+
		"\u0081\7d\2\2\u0081\u0082\7t\2\2\u0082\u0083\7g\2\2\u0083\u0084\7c\2\2"+
		"\u0084\u0085\7m\2\2\u0085\20\3\2\2\2\u0086\u0087\7e\2\2\u0087\u0088\7"+
		"q\2\2\u0088\u0089\7p\2\2\u0089\u008a\7v\2\2\u008a\u008b\7k\2\2\u008b\u008c"+
		"\7p\2\2\u008c\u008d\7w\2\2\u008d\u008e\7g\2\2\u008e\22\3\2\2\2\u008f\u0090"+
		"\7t\2\2\u0090\u0091\7g\2\2\u0091\u0092\7v\2\2\u0092\u0093\7w\2\2\u0093"+
		"\u0094\7t\2\2\u0094\u0095\7p\2\2\u0095\24\3\2\2\2\u0096\u009b\5O(\2\u0097"+
		"\u009a\5O(\2\u0098\u009a\5Q)\2\u0099\u0097\3\2\2\2\u0099\u0098\3\2\2\2"+
		"\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\26"+
		"\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u00a2\5S*\2\u009f\u00a1\5Q)\2\u00a0"+
		"\u009f\3\2\2\2\u00a1\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2"+
		"\2\2\u00a3\30\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a9\7\62\2\2\u00a6\u00a8"+
		"\5U+\2\u00a7\u00a6\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9"+
		"\u00aa\3\2\2\2\u00aa\32\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00ae\5Y-\2"+
		"\u00ad\u00af\5W,\2\u00ae\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00ae"+
		"\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\34\3\2\2\2\u00b2\u00b6\5\27\f\2\u00b3"+
		"\u00b6\5\31\r\2\u00b4\u00b6\5\33\16\2\u00b5\u00b2\3\2\2\2\u00b5\u00b3"+
		"\3\2\2\2\u00b5\u00b4\3\2\2\2\u00b6\36\3\2\2\2\u00b7\u00b8\7*\2\2\u00b8"+
		" \3\2\2\2\u00b9\u00ba\7+\2\2\u00ba\"\3\2\2\2\u00bb\u00bc\7]\2\2\u00bc"+
		"$\3\2\2\2\u00bd\u00be\7_\2\2\u00be&\3\2\2\2\u00bf\u00c0\7}\2\2\u00c0("+
		"\3\2\2\2\u00c1\u00c2\7\177\2\2\u00c2*\3\2\2\2\u00c3\u00c4\7>\2\2\u00c4"+
		",\3\2\2\2\u00c5\u00c6\7>\2\2\u00c6\u00c7\7?\2\2\u00c7.\3\2\2\2\u00c8\u00c9"+
		"\7@\2\2\u00c9\60\3\2\2\2\u00ca\u00cb\7@\2\2\u00cb\u00cc\7?\2\2\u00cc\62"+
		"\3\2\2\2\u00cd\u00ce\7-\2\2\u00ce\64\3\2\2\2\u00cf\u00d0\7/\2\2\u00d0"+
		"\66\3\2\2\2\u00d1\u00d2\7,\2\2\u00d28\3\2\2\2\u00d3\u00d4\7\61\2\2\u00d4"+
		":\3\2\2\2\u00d5\u00d6\7\'\2\2\u00d6<\3\2\2\2\u00d7\u00d8\7(\2\2\u00d8"+
		"\u00d9\7(\2\2\u00d9>\3\2\2\2\u00da\u00db\7~\2\2\u00db\u00dc\7~\2\2\u00dc"+
		"@\3\2\2\2\u00dd\u00de\7#\2\2\u00deB\3\2\2\2\u00df\u00e0\7<\2\2\u00e0D"+
		"\3\2\2\2\u00e1\u00e2\7=\2\2\u00e2F\3\2\2\2\u00e3\u00e4\7.\2\2\u00e4H\3"+
		"\2\2\2\u00e5\u00e6\7?\2\2\u00e6J\3\2\2\2\u00e7\u00e8\7?\2\2\u00e8\u00e9"+
		"\7?\2\2\u00e9L\3\2\2\2\u00ea\u00eb\7#\2\2\u00eb\u00ec\7?\2\2\u00ecN\3"+
		"\2\2\2\u00ed\u00ee\t\2\2\2\u00eeP\3\2\2\2\u00ef\u00f0\t\3\2\2\u00f0R\3"+
		"\2\2\2\u00f1\u00f2\t\4\2\2\u00f2T\3\2\2\2\u00f3\u00f4\t\5\2\2\u00f4V\3"+
		"\2\2\2\u00f5\u00f6\t\6\2\2\u00f6X\3\2\2\2\u00f7\u00f8\7\62\2\2\u00f8\u00f9"+
		"\t\7\2\2\u00f9Z\3\2\2\2\u00fa\u00fc\t\b\2\2\u00fb\u00fa\3\2\2\2\u00fc"+
		"\u00fd\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff\3\2"+
		"\2\2\u00ff\u0100\b.\2\2\u0100\\\3\2\2\2\u0101\u0103\7\17\2\2\u0102\u0104"+
		"\7\f\2\2\u0103\u0102\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0107\3\2\2\2\u0105"+
		"\u0107\7\f\2\2\u0106\u0101\3\2\2\2\u0106\u0105\3\2\2\2\u0107\u0108\3\2"+
		"\2\2\u0108\u0109\b/\2\2\u0109^\3\2\2\2\u010a\u010b\7\61\2\2\u010b\u010c"+
		"\7,\2\2\u010c\u0110\3\2\2\2\u010d\u010f\13\2\2\2\u010e\u010d\3\2\2\2\u010f"+
		"\u0112\3\2\2\2\u0110\u0111\3\2\2\2\u0110\u010e\3\2\2\2\u0111\u0113\3\2"+
		"\2\2\u0112\u0110\3\2\2\2\u0113\u0114\7,\2\2\u0114\u0115\7\61\2\2\u0115"+
		"\u0116\3\2\2\2\u0116\u0117\b\60\2\2\u0117`\3\2\2\2\u0118\u0119\7\61\2"+
		"\2\u0119\u011a\7\61\2\2\u011a\u011e\3\2\2\2\u011b\u011d\n\t\2\2\u011c"+
		"\u011b\3\2\2\2\u011d\u0120\3\2\2\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2"+
		"\2\2\u011f\u0121\3\2\2\2\u0120\u011e\3\2\2\2\u0121\u0122\b\61\2\2\u0122"+
		"b\3\2\2\2\16\2\u0099\u009b\u00a2\u00a9\u00b0\u00b5\u00fd\u0103\u0106\u0110"+
		"\u011e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}