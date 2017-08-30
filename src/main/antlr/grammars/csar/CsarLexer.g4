lexer grammar CsarLexer;

@lexer::header {
    package grammars.csar;
}

// Keywords
SELECT: 'SELECT' | 'select';
FROM: 'FROM' | 'from';
CONTAINS: 'CONTAINS' | 'contains';
AND: 'AND' | 'and' | '&&';
OR: 'OR' | 'or' | '||';
NOT: 'NOT' | 'not' | '!';

DEF: 'DEFINITION' | 'definition' | 'def' | 'd';
USE: 'USAGE' | 'usage' | 'use' | 'u';

CLASS: 'CLASS' | 'class' | 'cls' | 'c';
METHOD: 'METHOD' | 'method' | 'm' | 'FUNCTION' | 'function' | 'func' | 'fn' | 'f';

PUBLIC: 'public' | 'pub';
PRIVATE: 'private' | 'priv';
PROTECTED: 'protected' | 'prot';
PACKAGE_PRIVATE: 'pkgpriv' | 'none';
STATIC: 'static';
FINAL: 'final';
ABSTRACT: 'abstract';
INTERFACE: 'interface';
STRICTFP: 'strictfp';
ANONYMOUS: 'anonymous' | 'anon';
INNER: 'inner';
SUPER: 'super';
OVERRIDDEN: 'overridden';
THROWS: 'throws';

// Symbols
SPACE: ' ';
COLON: ':';
DOLLAR: '$';
UNDERSCORE: '_';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
WS_ESCAPE_SEQUENCES: [\t\r\n]+ -> channel(HIDDEN);

// Language elements
IDENTIFIER_NAME: JAVA_LETTER JAVA_LETTER_OR_DIGIT*;
NUMBER: DIGIT+;

// Fragments
fragment TEXT: [a-zA-Z];
fragment DIGIT: [0-9];

// The following two rules are taken from: https://github.com/antlr/grammars-v4/blob/master/java8/Java8.g4
fragment JAVA_LETTER
	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment JAVA_LETTER_OR_DIGIT
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;
