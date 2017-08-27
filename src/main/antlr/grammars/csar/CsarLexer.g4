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

PUBLIC: 'public';
PRIVATE: 'private';
PROTECTED: 'protected';
PACKAGE_PRIVATE: 'none' | 'pkgpriv';
STATIC: 'static';
FINAL: 'final';

// Symbols
SPACE: ' ';
COLON: ':';
DOLLAR: '$';
UNDERSCORE: '_';
ESCAPE_SEQUENCES: [\t\r\n]+ -> channel(HIDDEN);

// Language elements
IDENTIFIER_NAME: (TEXT | DOLLAR | UNDERSCORE) (TEXT | DIGIT | DOLLAR | UNDERSCORE)*; // TODO all unicode except space

// Fragments
fragment TEXT: [a-zA-Z];
fragment DIGIT: [0-9];
