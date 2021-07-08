grammar SysY;

@header {
package antlr;
}

compUnit
    :    (decl|funcDef) +
    ;

decl
    :   constDecl
    |   varDecl
    ;

constDecl
    :   'const' bType constDef (','constDef)* ';'
    ;

bType
    :   'int'
    ;

constDef
    :   Identifier ('['constExp']')* '=' constInitVal
    ;

constInitVal
    :   constExp
    |   '{' (constInitVal(','constInitVal)*)? '}'
    ;
varDecl
    :   bType varDef (','varDef)* ';'
    ;
varDef
    :   bType varDecl ('['constExp']')*
    |   bType varDecl ('['constExp']')* '=' initVal
    ;
initVal
    :   exp
    |   '{'(initVal(','initVal)*)?'}'
    ;
funcDef
    :   funcType Identifier '('(funcFParams)?')' block
    ;
funcType
    :   'void'
    |   'int'
    ;
funcFParams
    :   funcFParam (','funcFParam)*
    ;
funcFParam
    :   bType Identifier ('['']'('['exp']')*)?
    ;
block
    :   '{'(blockItem)*'}'
    ;
blockItem
    :   decl
    |   stmt
    ;
stmt
    :   lVal '=' exp ';'
    |   (exp)? ';'
    |   block
    |   'if' '('cond')' stmt ('else'stmt)?
    |   'while' '('cond')' stmt
    |   'break'';'
    |   'continue'';'
    |   'return' (exp)? ';'
    ;
exp
    :   addExp
    ;
cond
    :   lOrExp
    ;
lVal
    :   Identifier ('['exp']')*
    ;
primaryExp
    :   '(' exp ')'
    |   lVal
    |   number
    ;
number
    :   Integer_const
    ;
unaryExp
    :   primaryExp | Identifier '(' (funcRParams)? ')'
    |   unaryOp unaryExp
    ;
unaryOp
    :   '+'|'-'|'!'
    ;

funcRParams
    :   exp (','exp)*
    ;
mulExp
    :   unaryExp
    |   mulExp ('*'|'/'|'%') unaryExp
    ;
addExp
    :   mulExp
    |   addExp ('+'|'-') mulExp
    ;
relExp
    :   addExp
    |   relExp ('<='|'>='|'<'|'>') addExp
    ;
eqExp
    :   relExp
    |   eqExp ('=='|'!=') relExp
    ;
lAndExp
    :   eqExp
    |   lAndExp '&&' eqExp
    ;
lOrExp
    :   lAndExp
    |   lOrExp '||' lAndExp
    ;
constExp
    :   addExp
    ;

//  标识符
Identifier
    :   Nondigit (Nondigit|Digit)*
    ;
//  十进制
Decimal_const
    :   NonZeroDigit Digit*
    ;
//  八进制
Octal_const
    :   '0' OctalDigit*
    ;
//  十六进制
Hexadecimal_cosnt
    :   HexPrefix  HexDigit+
    ;
//  整型
Integer_const
    :   Decimal_const
    |   Octal_const
    |   Hexadecimal_cosnt
    ;


LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';

Plus : '+';
Minus : '-';
Star : '*';
Div : '/';
Mod : '%';

And : '&&';
Or  : '||';
Not : '!';

Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';

Equal : '==';
NotEqual : '!=';


fragment Nondigit
    :   [a-zA-Z_]
    ;
fragment Digit
    :   [0-9]
    ;
fragment NonZeroDigit
    :   [1-9]
    ;
fragment OctalDigit
    :   [0-7];
fragment HexDigit
    :   [0-9A-Fa-f]
    ;
fragment HexPrefix
    :   '0'[xX]
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;


