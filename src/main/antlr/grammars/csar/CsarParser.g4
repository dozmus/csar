parser grammar CsarParser;

@parser::header {
    package grammars.csar;
}

options {
    tokenVocab='grammars/csar/CsarLexer';
}

// Csar query (top-level)
// TODO allow NOT in front of more elements, e.g. ABSTRACT
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
// TODO allow escaping S_QUOTE in comments

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
expr: content; // XXX this should be parsed further at a language-specific level
