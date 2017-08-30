parser grammar CsarParser;

@parser::header {
    package grammars.csar;
}

options {
    tokenVocab='grammars/csar/CsarLexer';
}

// Csar query (top-level)
// TODO allow NOT in front of more elements, e.g. ABSTRACT
csarQuery: (SELECT SPACE)? searchQuery (SPACE FROM SPACE IDENTIFIER_NAME)? EOF; // TODO impl refactor rule
// TODO allow IDENTIFIER_NAME list for FROM?
searchQuery: languageElement (SPACE domainQueryPart)?;
domainQueryPart: CONTAINS SPACE (NOT SPACE)? languageElement domainQueryRest*; // TODO allow parenthesis?
domainQueryRest: SPACE (AND | OR) SPACE (NOT SPACE)? languageElement;

languageElement: clazz | method;

// Class
clazz: CLASS languageElementHeader classModifiers IDENTIFIER_NAME superClassList?;
classModifiers: ((ABSTRACT | INTERFACE) SPACE)? (STRICTFP SPACE)? (ANONYMOUS SPACE)? (INNER SPACE)?;
superClassList: LPAREN SPACE* typeList SPACE* RPAREN;

// Method
method: METHOD languageElementHeader (OVERRIDDEN SPACE)? (type SPACE)? IDENTIFIER_NAME
        (SPACE? methodParameters)? (SPACE methodThrownExceptions)? (SPACE SUPER SPACE* superClassList)?;
methodParameters: LPAREN SPACE* (NUMBER | typeList | namedTypeList) SPACE* RPAREN;
methodThrownExceptions: THROWS SPACE* LPAREN SPACE* typeList SPACE* RPAREN;

// Helpers
languageElementHeader: COLON (DEF | USE) COLON (visibilityModifier SPACE)? (STATIC SPACE)? (FINAL SPACE)?; // TODO rename rule
typeList: type (SPACE* COMMA SPACE* type)*;
namedTypeList: type SPACE+ IDENTIFIER_NAME (SPACE* COMMA SPACE* type SPACE+ IDENTIFIER_NAME)*;
type: IDENTIFIER_NAME; // XXX is this even fine?
visibilityModifier: PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE;
