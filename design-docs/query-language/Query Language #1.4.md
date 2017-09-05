# Query Language (Draft #1.4)
## Syntax (ANTLR4)
```
/**
 * Lexer
 */
lexer grammar CsarLexer;

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
CHANGE_PARAMETERS: 'changeparam';

// Misc
TRANSIENT: 'transient';
VOLATILE: 'volatile';
JAVADOC: 'javadoc';

// Symbols
SPACE: ' ';
COLON: ':';
COMMA: ',';
S_QUOTE: '\'';
LPAREN: '(';
RPAREN: ')';

// Language elements
IDENTIFIER_NAME: ((JAVA_LETTER | REGEX_WC) (JAVA_LETTER | DIGIT | REGEX_WC)*);
NUMBER: DIGIT+;

// Fall-back
CATCH_ALL: (.)+?;

// Fragments
fragment TEXT: [a-zA-Z];
fragment DIGIT: [0-9];
fragment REGEX_WC: '*' | '_';

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

/**
 * Parser
 */
parser grammar CsarParser;

// Csar query (top-level)
csarQuery: (SELECT SPACE)? languageElement (SPACE containsQuery)? (SPACE fromQuery)? (SPACE refactorQuery)? EOF;
containsQuery: CONTAINS SPACE (NOT SPACE)? languageElement containsQueryRest*; // TODO allow parentheses
containsQueryRest: SPACE (AND | OR) SPACE (NOT SPACE)? languageElement;
fromQuery: FROM SPACE typeList;
refactorQuery: REFACTOR SPACE refactorElement;

languageElement: clazz | method | variable | controlFlow | comment;
refactorElement: rename | changeParameters;

// Class
clazz: (CLASS_NV | CLASS_V) commonModifiers classModifiers identifierName superClassList?;
classModifiers: ((ABSTRACT | INTERFACE) SPACE)? (STRICTFP SPACE)? (ANONYMOUS SPACE)? (INNER SPACE)?;
superClassList: LPAREN SPACE* typeList SPACE* RPAREN;

// Method
method
    : METHOD commonModifiers (OVERRIDDEN SPACE)? (type SPACE)? identifierName
     (SPACE? methodParameters)? (SPACE methodThrownExceptions)? (SPACE SUPER SPACE* superClassList)?
    ;
methodParameters: LPAREN SPACE* (NUMBER | typeList | namedTypeList) SPACE* RPAREN;
methodThrownExceptions: THROWS SPACE* LPAREN SPACE* typeList SPACE* RPAREN;

// Variable
variable: instanceVariable | localVariable | paramVariable;
instanceVariable: INSTANCE commonModifiers instanceVariableModifiers (type SPACE)? identifierName;
localVariable: LOCAL COLON (DEF | USE) COLON (FINAL SPACE)? (type SPACE)? identifierName;
paramVariable: PARAM COLON (DEF | USE) COLON (FINAL SPACE)? (type SPACE)? identifierName;

// Control-flow
controlFlow: if0 | switch0 | while0 | dowhile | for0 | foreach | ternary | synchronized0;
if0: IF (LPAREN expr RPAREN)?;
switch0: SWITCH (LPAREN expr RPAREN | COLON identifierName)?;
while0: WHILE (LPAREN expr RPAREN)?;
dowhile: DOWHILE (LPAREN expr RPAREN)?;
for0: FOR;
foreach: FOREACH (COLON identifierName)?;
ternary: TERNARY;
synchronized0: SYNCHRONIZED (LPAREN expr RPAREN | COLON identifierName)?;

// Comment
comment: singleLineComment | multiLineComment;
singleLineComment: SINGLE_LINE_COMMENT (COLON S_QUOTE content S_QUOTE)?;
multiLineComment: MULTI_LINE_COMMENT (COLON (JAVADOC COLON)? S_QUOTE content S_QUOTE)?;

// Refactor
rename: RENAME COLON SPACE* identifierName;
changeParameters: CHANGE_PARAMETERS COLON SPACE* (typeList | namedTypeList);

// Helpers
commonModifiers: COLON (DEF | USE) COLON (visibilityModifier SPACE)? (STATIC SPACE)? (FINAL SPACE)?;

visibilityModifier: PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE;
instanceVariableModifiers: ((TRANSIENT | VOLATILE) SPACE)?;

type: identifierName;
typeList: type (SPACE* COMMA SPACE* type)*;
namedTypeList: type SPACE+ identifierName (SPACE* COMMA SPACE* type SPACE+ identifierName)*;
identifierName
    : IDENTIFIER_NAME | SELECT | CONTAINS | FROM | REFACTOR | DEF | USE | AND | OR | NOT | DOWHILE | TERNARY | RENAME
    | CHANGE_PARAMETERS | OVERRIDDEN | ANONYMOUS | INNER | JAVADOC | SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT
    | PACKAGE_PRIVATE | INSTANCE | LOCAL | PARAM | METHOD | CLASS_NV
    ;

content
    : (SELECT | CONTAINS | FROM | REFACTOR | DEF | USE | AND | OR | NOT | CLASS_NV | CLASS_V | METHOD | INSTANCE
        | LOCAL | PARAM | IF | SWITCH | WHILE | DOWHILE | FOR | FOREACH | TERNARY | SYNCHRONIZED | SINGLE_LINE_COMMENT
        | MULTI_LINE_COMMENT | PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE | STATIC | FINAL | ABSTRACT | CATCH_ALL
        | INTERFACE | STRICTFP | ANONYMOUS | INNER | SUPER | OVERRIDDEN | THROWS | RENAME | CHANGE_PARAMETERS
        | TRANSIENT | VOLATILE | JAVADOC | SPACE | COLON | COMMA | LPAREN | RPAREN | IDENTIFIER_NAME | NUMBER | S_QUOTE
      )*
    ;
expr: content;
```

## Problems with the Implementation
* The `expr` rule is very lenient, this is because the definition of an expression depends on a target programming language. This is intended to be parsed further at a language-specific level.
* Explicitly allowing the escaping of single quote in the comment rules would be optimal, but it works anyway.
* Allow more usage of `NOT`for increased expressiveness, i.e. `not final`, `not static`.

## Problems with the Syntax
* Cannot represent lambdas
* Cannot represent try-catch blocks
* Cannot represent throw (i.e. throwing an exception)
* Cannot represent computation/arithmetic (i.e. assignment, and addition)
* Cannot search for multiple elements at once, a top-level 'OR' operator would address this.  
  However, this conflicts with refactoring because: how do you rename two distinct elements to the same name, and such an action would be indicative of user error. One solution is to print an error message and terminate.
