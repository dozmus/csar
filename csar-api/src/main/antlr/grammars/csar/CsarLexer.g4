lexer grammar CsarLexer;

@lexer::header {
    package grammars.csar;
}

// Csar query keywords
SELECT: 'SELECT' | 'select';
CONTAINS: 'CONTAINS' | 'contains';
FROM: 'FROM' | 'from';
REFACTOR: 'REFACTOR' | 'refactor';

DEF: 'DEFINITION' | 'definition' | 'def' | 'd';
USE: 'USAGE' | 'usage' | 'use' | 'u';

// Logical operators
AND: 'AND' | 'and';
OR: 'OR' | 'or';
NOT: 'NOT' | 'not';

// Language elements
CLASS_NV: 'CLASS' | 'cls' | 'c';
CLASS_V: 'class'; // separated the java keyword from the non-keywords
METHOD: 'METHOD' | 'method' | 'm' | 'FUNCTION' | 'function' | 'func' | 'fn' | 'f';

INSTANCE: 'INSTANCE' | 'instance' | 'FIELD' | 'field' | 'i';
LOCAL: 'LOCAL' | 'local' | 'l';
PARAM: 'PARAM' | 'param' | 'p';

IF: 'if';
SWITCH: 'switch';
WHILE: 'while';
DOWHILE: 'dowhile';
FOR: 'for';
FOREACH: 'foreach';
TERNARY: 'ternary';
SYNCHRONIZED: 'synchronized';

SINGLE_LINE_COMMENT: 'slc';
MULTI_LINE_COMMENT: 'mlc';

// Class/method
PUBLIC: 'public';
PRIVATE: 'private';
PROTECTED: 'protected';
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

// Refactor
RENAME: 'rename';
CHANGE_PARAMETERS: 'changeparam' | 'changeparams';

// Misc
TRANSIENT: 'transient';
VOLATILE: 'volatile';
JAVADOC: 'javadoc';
REGEXP: 'REGEXP' | 'regexp' | 'REGEX' | 'regex';

// Symbols
SPACE: ' ';
COLON: ':';
COMMA: ',';
S_QUOTE: '\'';
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';

// Language elements
IDENTIFIER_NAME: JAVA_LETTER (JAVA_LETTER | DIGIT)*;
NUMBER: DIGIT+;

// Fall-back rule
CATCH_ALL: (.)+?;

// Fragments
fragment TEXT: [a-zA-Z];
fragment DIGIT: [0-9];

// The following rule is taken from: https://github.com/antlr/grammars-v4/blob/master/java8/Java8.g4
fragment JAVA_LETTER
    :   TEXT | [$_] // these are the "java letters" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;
