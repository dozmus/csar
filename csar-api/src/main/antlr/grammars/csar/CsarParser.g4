parser grammar CsarParser;

@parser::header {
    package grammars.csar;
}

options {
    tokenVocab='grammars/csar/CsarLexer';
}

// Csar query (top-level)
csarQuery: (SELECT SPACE)? statementDescriptor (SPACE containsQuery)? (SPACE fromQuery)? (SPACE refactorQuery)? EOF;
containsQuery: CONTAINS SPACE (NOT SPACE)? statementDescriptor containsQueryRest*;
containsQueryRest: SPACE (AND | OR) SPACE (NOT SPACE)? statementDescriptor;
fromQuery: FROM SPACE typeList;
refactorQuery: REFACTOR SPACE refactorDescriptor;

statementDescriptor: clazz | method | variable | controlFlow | statement | comment;
refactorDescriptor: rename | changeParameters;

// Class
clazz: (CLASS_NV | CLASS_V) commonModifiers classModifiers (identifierName | regexIdentifierName) superClassList?;
classModifiers: ((ABSTRACT | INTERFACE) SPACE)? (STRICTFP SPACE)? (ANONYMOUS SPACE)? (INNER SPACE)?;
superClassList: LPAREN SPACE* typeList SPACE* RPAREN;

// Method
method
    : METHOD commonModifiers (OVERRIDDEN SPACE)? (type SPACE)? (identifierName | regexIdentifierName)
     (SPACE? methodParameters)? (SPACE methodThrownExceptions)? (SPACE SUPER SPACE* superClassList)?
    ;
methodParameters: LPAREN SPACE* (NUMBER | paramTypeList | paramNamedTypeList) SPACE* RPAREN;
methodThrownExceptions: THROWS SPACE* LPAREN SPACE* typeList SPACE* RPAREN;
paramTypeList: (FINAL SPACE)? SPACE* type paramTypeListRest*;
paramTypeListRest: SPACE* COMMA (FINAL SPACE)? SPACE* type;
paramNamedTypeList: (FINAL SPACE)? type SPACE+ identifierName paramNamedTypeListRest*;
paramNamedTypeListRest: SPACE* COMMA (FINAL SPACE)? SPACE* type SPACE+ identifierName;

// Variable
variable: instanceVariable | localVariable | paramVariable;
instanceVariable: INSTANCE commonModifiers instanceVariableModifiers (type SPACE)? identifierName;
instanceVariableModifiers: ((TRANSIENT | VOLATILE) SPACE)?;
localVariable: LOCAL COLON (DEF | USE) COLON (FINAL SPACE)? (type SPACE)? identifierName;
paramVariable: PARAM COLON (DEF | USE) COLON (FINAL SPACE)? (type SPACE)? identifierName;

// Control Flow
controlFlow: if0 | switch0 | while0 | dowhile | for0 | foreach | ternary;
if0: IF (LPAREN expr RPAREN)?;
switch0: SWITCH (LPAREN expr RPAREN | COLON identifierName)?;
while0: WHILE (LPAREN expr RPAREN)?;
dowhile: DOWHILE (LPAREN expr RPAREN)?;
for0: FOR;
foreach: FOREACH (COLON identifierName)?;
ternary: TERNARY;

// Statement
statement: synchronized0;
synchronized0: SYNCHRONIZED (LPAREN expr RPAREN | COLON identifierName)?;

// Comment
comment: singleLineComment | multiLineComment;
singleLineComment: SINGLE_LINE_COMMENT (COLON S_QUOTE content S_QUOTE)?;
multiLineComment: MULTI_LINE_COMMENT (COLON JAVADOC)? (COLON S_QUOTE content S_QUOTE)?;

// Refactor
rename: RENAME COLON SPACE* identifierName;
changeParameters: CHANGE_PARAMETERS COLON SPACE* namedTypeList;

// Helpers
commonModifiers: COLON (DEF | USE) COLON (visibilityModifier SPACE)? (STATIC SPACE)? (FINAL SPACE)?;
visibilityModifier: PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE;

type: identifierName (LBRACK RBRACK)*;
typeList: type (SPACE* COMMA SPACE* type)*;
namedTypeList: type SPACE+ identifierName (SPACE* COMMA SPACE* type SPACE+ identifierName)*;
identifierName
    : IDENTIFIER_NAME | SELECT | CONTAINS | FROM | REFACTOR | DEF | USE | AND | OR | NOT | DOWHILE | TERNARY | RENAME
    | CHANGE_PARAMETERS | OVERRIDDEN | ANONYMOUS | INNER | JAVADOC | SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT
    | PACKAGE_PRIVATE | INSTANCE | LOCAL | PARAM | METHOD | CLASS_NV | REGEXP
    ;
regexIdentifierName: REGEXP LPAREN (content | identifierName) RPAREN;

content
    : (SELECT | CONTAINS | FROM | REFACTOR | DEF | USE | AND | OR | NOT | CLASS_NV | CLASS_V | METHOD | INSTANCE
        | LOCAL | PARAM | IF | SWITCH | WHILE | DOWHILE | FOR | FOREACH | TERNARY | SYNCHRONIZED | SINGLE_LINE_COMMENT
        | MULTI_LINE_COMMENT | PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE | STATIC | FINAL | ABSTRACT | CATCH_ALL
        | INTERFACE | STRICTFP | ANONYMOUS | INNER | SUPER | OVERRIDDEN | THROWS | RENAME | CHANGE_PARAMETERS
        | TRANSIENT | VOLATILE | JAVADOC | SPACE | COLON | COMMA | LPAREN | RPAREN | IDENTIFIER_NAME | NUMBER | S_QUOTE
        | LBRACK | RBRACK | REGEXP
      )*
    ;
expr: content; // XXX this should be parsed further at a language-specific level
