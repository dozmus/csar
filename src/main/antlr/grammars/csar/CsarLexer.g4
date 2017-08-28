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
ESCAPE_SEQUENCES: [\t\r\n]+ -> channel(HIDDEN);

// Language elements
IDENTIFIER_NAME: (TEXT | DOLLAR | UNDERSCORE) (TEXT | DIGIT | DOLLAR | UNDERSCORE)*; // TODO all unicode except space
NUMBER: DIGIT+;

// Fragments
fragment TEXT: [a-zA-Z];
fragment DIGIT: [0-9];
