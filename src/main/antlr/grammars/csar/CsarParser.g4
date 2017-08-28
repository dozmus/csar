parser grammar CsarParser;

@parser::header {
    package grammars.csar;
}

options {
    tokenVocab='grammars/csar/CsarLexer';
}

// Csar query (top-level)
// TODO allow NOT in front of more elements, e.g. ABSTRACT
csarQuery: (SELECT SPACE)? searchQuery (SPACE FROM languageElement)? EOF; // TODO impl refactor rule, allow list for FROM?
searchQuery: languageElement (SPACE domainQueryPart)?;
domainQueryPart: CONTAINS (SPACE NOT? languageElement domainQueryRest*); // TODO allow parenthesis?
domainQueryRest: SPACE (AND | OR) SPACE NOT? languageElement;

languageElement: clazz | method;

// Class
clazz: CLASS languageElementHeader classModifiers IDENTIFIER_NAME superClassList?;
classModifiers: ((ABSTRACT | INTERFACE) SPACE)? (STRICTFP SPACE)? (ANONYMOUS SPACE)? (INNER SPACE)?;
superClassList: LPAREN typeList RPAREN;

// Method
method: METHOD languageElementHeader (OVERRIDDEN SPACE)? (type SPACE)? IDENTIFIER_NAME
        (SPACE methodParameters)? (SPACE methodThrownExceptions)? (SPACE SUPER superClassList)?;
methodParameters: LPAREN SPACE? (NUMBER | typeList | namedTypeList) SPACE? RPAREN;
methodThrownExceptions: THROWS SPACE typeList;

// Helpers
languageElementHeader: COLON (DEF | USE) COLON (visibilityModifier SPACE)? (STATIC SPACE)? (FINAL SPACE)?; // TODO rename rule
typeList: type (SPACE? COMMA SPACE? type)*;
namedTypeList: type SPACE TEXT (SPACE? COMMA SPACE? type SPACE TEXT)*;
type: TEXT; // XXX is this even fine?
visibilityModifier: PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE;
