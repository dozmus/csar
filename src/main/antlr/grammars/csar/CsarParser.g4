parser grammar CsarParser;

@parser::header {
    package grammars.csar;
}

options {
    tokenVocab='grammars/csar/CsarLexer';
}

csarQuery: (SELECT SPACE)? searchQuery EOF; // TODO implement fully
searchQuery: languageElement; // TODO implement fully

languageElement: clazz | method;

clazz: CLASS languageElementHeader IDENTIFIER_NAME; // TODO implement fully
method: METHOD languageElementHeader IDENTIFIER_NAME;// TODO implement fully

// Helpers
languageElementHeader: COLON (DEF | USE) COLON (visibilityModifier SPACE)? (STATIC SPACE)? (FINAL SPACE)?;
visibilityModifier: PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE;
