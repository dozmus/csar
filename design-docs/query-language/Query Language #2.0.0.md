# Query Language (Draft #2.0.0)
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
LENIENT: 'LENIENT';

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
ENUM_NV: 'ENUM' | 'e';
ENUM_V: 'enum'; // separated the java keyword from the non-keywords
ANNOTATION: 'annotation' | 'anon' | 'a';
CONSTRUCTOR: 'CONSTRUCTOR' | 'constructor' | 'cons';
STATIC_CONSTRUCTOR: 'STATICCONSTRUCTOR' | 'staticconstructor' | 'staticcons';

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
EXTENDS: 'extends';
OVERRIDDEN: 'overridden';
THROWS: 'throws';
GOTO: 'goto';
DEFAULT: 'default';
THROW: 'throw';
PKG: 'pkg';

// Refactor
RENAME: 'rename';
CHANGE_PARAMETERS: 'changeparam';
MOVE: 'move';
REDUCE_DUPLICATES: 'reducedups';

// Misc
ENUM_CONST: 'enumconst' | 'enum_c' | 'enumc' | 'ec';
TRANSIENT: 'transient';
VOLATILE: 'volatile';
JAVADOC: 'javadoc';
LAMBDA: 'lambda';
TRY_CATCH: 'trycatch';
TRY_WRES_CATCH: 'trywres';
CATCHES: 'catches';
STUB: 'stub';

// Symbols
SPACE: ' ';
COLON: ':';
COMMA: ',';
S_QUOTE: '\'';
LPAREN: '(';
RPAREN: ')';
LT: '<';
GT: '>';
QUESTION: '?';
ELLIPSIS: '...';

// Language elements
IDENTIFIER_NAME: ((JAVA_LETTER | REGEX_WC) (JAVA_LETTER | DIGIT | REGEX_WC)*);
NUMBER: DIGIT+;

// Fall-back rule
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
csarQuery: (SELECT SPACE)? elementDescriptor (SPACE containsQuery)? (SPACE fromQuery)? (SPACE refactorQuery)? (SPACE LENIENT)? EOF;
containsQuery: CONTAINS SPACE containsQueryBody;
fromQuery: FROM SPACE fromQueryBody;
refactorQuery: REFACTOR SPACE refactorDescriptor;

containsQueryBody: containsQueryBody
                | LPAREN SPACE* containsQueryBody SPACE* RPAREN
                | (NOT SPACE)? elementDescriptor
                | containsQueryBody SPACE (AND | OR) SPACE containsQueryBody
                ;

fromQueryBody: fromQueryTarget (SPACE* COMMA SPACE* fromQueryTarget)*;
fromQueryTarget: (PKG COLON identifierName) | (classLexerRule COLON identifierName); // packages/types to search within

elementDescriptor: typeDecl | method | variable | constructor | controlFlow | statement | comment;
refactorDescriptor: rename | changeParameters | reduceDuplicates | move;

// Type declaration
typeDecl
        : (ENUM_NV | ENUM_V | ANNOTATION | classLexerRule) commonModifiers typeDeclModifiers (genericTypeParameterList SPACE)?
        identifierName superClassList?;
typeDeclModifiers: (NOT? (ABSTRACT | INTERFACE) SPACE)? (NOT? STRICTFP SPACE)? (NOT? ANONYMOUS SPACE)? (NOT? INNER SPACE)?;
superClassList: LPAREN SPACE* typeList SPACE* RPAREN;

// Method
method
    : METHOD commonModifiers (NOT? OVERRIDDEN SPACE)? (NOT? ABSTRACT SPACE)? (NOT? STRICTFP SPACE)? (NOT? DEFAULT SPACE)?
     (NOT? STUB SPACE)? (type SPACE)? (genericTypeParameterList SPACE)? identifierName (SPACE? methodParameters)?
     (SPACE methodThrownExceptions)? (SPACE SUPER SPACE* superClassList)?
    ;
methodParameters: LPAREN SPACE* (NUMBER | paramTypeList | paramNamedTypeList) SPACE* RPAREN;
methodThrownExceptions: THROWS SPACE* LPAREN SPACE* typeList SPACE* RPAREN;
paramTypeList: (NOT? FINAL SPACE)? SPACE* type paramTypeListRest* paramTypeListEnd?;
paramTypeListRest: SPACE* COMMA (NOT? FINAL SPACE)? SPACE* type;
paramTypeListEnd: SPACE* COMMA (NOT? FINAL SPACE)? SPACE* type ELLIPSIS?;
paramNamedTypeList: (NOT? FINAL SPACE)? type SPACE+ identifierName paramNamedTypeListRest* paramTypeListEnd?;
paramNamedTypeListRest: SPACE* COMMA (NOT? FINAL SPACE)? SPACE* type SPACE+ identifierName;
paramNamedTypeListEnd: SPACE* COMMA (NOT? FINAL SPACE)? SPACE* type ELLIPSIS? SPACE+ identifierName;

// Constructor
constructor: CONSTRUCTOR commonModifiers (NOT? OVERRIDDEN SPACE)? (type SPACE)? identifierName
     (SPACE? methodParameters)? (SPACE methodThrownExceptions)? (SPACE SUPER SPACE* superClassList)?
    ;

// Static constructor
staticConstructor: STATIC_CONSTRUCTOR;

// Variable
variable: instanceVariable | localVariable | paramVariable;
instanceVariable: INSTANCE commonModifiers instanceVariableModifiers (type SPACE)? identifierName;
instanceVariableModifiers: (NOT? (TRANSIENT | VOLATILE) SPACE)?;
localVariable: LOCAL COLON searchType COLON (NOT? FINAL SPACE)? (type SPACE)? identifierName;
paramVariable: PARAM COLON searchType COLON (NOT? FINAL SPACE)? (type SPACE)? identifierName;

// Control Flow
controlFlow: if0 | switch0 | while0 | dowhile | for0 | foreach | tryCatch | tryWithResCatch;
if0: IF (exprParen)?;
switch0: SWITCH (exprParen | COLON identifierName)?;
while0: WHILE (exprParen)?;
dowhile: DOWHILE (exprParen)?;
for0: FOR;
foreach: FOREACH (COLON identifierName)?;
tryCatch: TRY_CATCH (CATCHES caughtList)?;
tryWithResCatch: TRY_WRES_CATCH (LPAREN expr RPAREN (CATCHES caughtList)?)?;
caughtList: NUMBER | paramTypeList | paramNamedTypeList;

// Statements
statement: synchronized0 | goto0 | throw0 | lambda | enumConst | ternary;
synchronized0: SYNCHRONIZED (exprParen | COLON identifierName)?;
goto0: GOTO (COLON identifierName)?;
throw0: THROW (COLON identifierName SPACE? (methodParameters? | LPAREN RPAREN) (SPACE methodThrownExceptions)?);
lambda: LAMBDA (COLON methodParameters)?;
enumConst: ENUM_CONST ((COLON searchType)? COLON identifierName methodParameters?)?;
ternary: TERNARY;

// Comment
comment: singleLineComment | multiLineComment;
singleLineComment: SINGLE_LINE_COMMENT (COLON S_QUOTE content S_QUOTE)?;
multiLineComment: MULTI_LINE_COMMENT (COLON JAVADOC)? (COLON S_QUOTE content S_QUOTE)?;

// Refactor
rename: RENAME COLON SPACE* identifierName;
changeParameters: CHANGE_PARAMETERS COLON SPACE* (typeList | namedTypeList);
move: MOVE COLON SPACE* (S_QUOTE content S_QUOTE) SPACE+ (S_QUOTE content S_QUOTE);
reduceDuplicates: REDUCE_DUPLICATES COLON SPACE* (S_QUOTE content S_QUOTE);

// Helpers
classLexerRule: CLASS_NV | CLASS_V;
searchType: DEF | USE;
commonModifiers: COLON searchType COLON (NOT? visibilityModifier SPACE)? (NOT? STATIC SPACE)? (NOT? FINAL SPACE)?;
visibilityModifier: PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE;

type: identifierName (LT genericIdentifierName GT)? (LBRACK RBRACK)*;
typeList: type (SPACE* COMMA SPACE* type)*;
namedTypeList: type SPACE+ identifierName (SPACE* COMMA SPACE* type SPACE+ identifierName)*;
identifierName
    : IDENTIFIER_NAME | SELECT | CONTAINS | FROM | REFACTOR | DEF | USE | AND | OR | NOT | DOWHILE | TERNARY | RENAME
    | CHANGE_PARAMETERS | OVERRIDDEN | ANONYMOUS | INNER | JAVADOC | SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT
    | PACKAGE_PRIVATE | INSTANCE | LOCAL | PARAM | METHOD | CLASS_NV | MOVE | REDUCE_DUPLICATES | CONSTRUCTOR
    | STATIC_CONSTRUCTOR | LAMBDA | ENUM_NV | ANNOTATION | ENUM_CONST | TRY_CATCH | TRY_WRES_CATCH | CATCHES
    | PKG | LENIENT | STUB
    ;

genericIdentifierName: (QUESTION SPACE ((EXTENDS | SUPER) SPACE identifierName)?) | identifierName;
genericTypeParameterList: LT (genericTypeParameter (SPACE* COMMA SPACE* genericTypeParameter)*)? GT;
genericTypeParameter: identifierName (SPACE (EXTENDS | SUPER) SPACE identifierName)?;

expr: content;
exprParen: LPAREN expr RPAREN;

content
    : (SELECT | CONTAINS | FROM | REFACTOR | DEF | USE | AND | OR | NOT | CLASS_NV | CLASS_V | METHOD | INSTANCE
        | LOCAL | PARAM | IF | SWITCH | WHILE | DOWHILE | FOR | FOREACH | TERNARY | SYNCHRONIZED | SINGLE_LINE_COMMENT
        | MULTI_LINE_COMMENT | PUBLIC | PRIVATE | PROTECTED | PACKAGE_PRIVATE | STATIC | FINAL | ABSTRACT | CATCH_ALL
        | INTERFACE | STRICTFP | ANONYMOUS | INNER | SUPER | OVERRIDDEN | THROWS | RENAME | CHANGE_PARAMETERS
        | TRANSIENT | VOLATILE | JAVADOC | SPACE | COLON | COMMA | LPAREN | RPAREN | IDENTIFIER_NAME | NUMBER | S_QUOTE
        | LBRACK | RBRACK | MOVE | REDUCE_DUPLICATES | GOTO | DEFAULT | CONSTRUCTOR | STATIC_CONSTRUCTOR | THROW
        | ELLIPSIS | LAMBDA | ENUM_NV | ENUM_V | ANNOTATION | ENUM_CONST | TRY_CATCH | TRY_WRES_CATCH | CATCHES
        | PKG | LT | GT | EXTENDS | QUESTION | LENIENT | STUB
      )*
    ;
```

## Problems with the Syntax
* More leniency in csar query grammar
* Allow RegEx to be used to represent types and identifier names everywhere
* Allow fully qualified types
* Cannot represent a variety of statements
* Cannot represent annotations (usages)
* Cannot represent computation/arithmetic (i.e. assignment, and addition)
* Cannot distinguish extended classes from implemented interfaces (they are treated the same, for simplicity)
* Cannot search for multiple elements at once, a top-level 'OR' operator would address this.  
  However, this conflicts with refactoring because: how do you rename two distinct elements to the same name, and such an action would be indicative of user error.
  One solution to this is to print an error message and terminate.
* Note: The syntax will need to be extended to support each additional programming language.

## Use Cases
The use-cases will detail why and how end users might use this tool.  
Each will be complemented with a (S)upported tag, (P)artially supported tag, or an (U)nsupported tag.
These indicate if the tool will aim to handle searching/refactoring/etc of them.

### Search
#### Fundamental
* **Task (P)**: Remove all use of a deprecated class. Suppose this is replacing `Calendar` with `LocalDateTime`.  
  **Solution**: This is not something the tool aims to completely address, but it can find all usages with `class:use:Calendar` to aid the process.
* **Task (S)**: Find all use of an interface called `HttpProvider`.  
  **Solution**: `class:use:interface HttpProvider`
* **Task (S)**: Find all implementations of a method in an abstract class or interface, called `SuperClass`.  
  **Solution**: `method:def:overridden * super(SuperClass)`
* **Task (S)**: Find all use of a method called `add`. This might be to: ensure pending changes have no negative effects, change its signature manually or split the method into two.  
  **Solution**: `method:use:add`  
  It is possible to also apply refinements such as: `method:u:add(2)`, `method:u:add(int, int)`, and `method:u:public static int add(int a, int b)`.
* **Task (S)**: Find the definition of a method whose name starts with `check` to inspect it or change its signature.  
  **Solution**: `method:def:check*`, or more compactly as `fn:d:check*`.  
* **Task (S)**: Find all use of a method called `add` whose signature states that it throws an exception called `ArithmeticException`.  
  **Solution**: `method:use:add throws(ArithmeticException)`

#### Variables
* **Task (U)**: Find all use of an instance variable (aka field). This might be to ensure pending changes have no negative effects.  
  **Solution**: `instance:u:result`, or refine it further to `instance:u:int result`.  
  Note: This is analogous to searching for parameters and local variables.
* **Task (U)**: Find all variables in a class called `MyClass`.  
  **Solution**: Not supported.

#### Control Flow
* **Task (U)**: Remove language feature usage to reduce the version requirements of a program. Suppose the initial target of this process are String switch statements (introduced in Java 1.7).  
  **Solution**: `switch:String`.
* **Task (U)**: Find all implementations of an abstract class or interface, called `SuperClass`.  
  **Solution**: `class:def:* super(SuperClass)`.
* **Task (U)**: Find all lambda expressions in a class called `MyClass`.  
  **Solution**: Not supported yet.
* **Task (U)**: Find all for-each statements with a specified iterated collection's type.  
  **Solution**: `foreach:CollectionType`
* **Task (U)**: Improve code by removing the highly stigmatised `goto` statement.  
  **Solution**: Not supported.
* **Task (U)**: Find all if statements with a certain expression, suppose `value() == 3`.  
  **Solution**: `if(value() == 3)`
* **Task (U)**: Find all for-loops.  
  **Solution**: `for`
* **Task (U)**: Find all ternary statements.  
  **Solution**: `ternary`
* **Task (U)**: Find all synchronization blocks, to examine the correctness of a multi-threaded program.  
  **Solution**: `synchronized`

#### Comments
* **Task (U)**: Find all 'TODO' comments (i.e. comments starting with the word `TODO`). This might be because: they are to be collated in bug tracking software or to find one to assign to a team member.  
  **Solution**: `slc:'TODO *'`
* **Task (U)**: Find all multi-line comments which are javadocs for a project-wide rewriting of them.  
  **Solution**: `mlc:javadoc`

### Refactor
* **Task (S)**: Rename a method called `add` in class `MathHelper` to `addInt`.  
  **Solution**: `method:def:add FROM MathHelper REFACTOR rename:addInt`
* **Task (S)**: Change a method called `add`'s parameters from `(int x, int y)` to `(Number a, Number b)`.
  **Solution**: `method:def:add(int x, int y) REFACTOR changeparam:(Number a, Number b)`
* **Task (U)**: Find and remove duplicates (by creating a new method with the appropriate signature and calling it).
  **Solution**: `reducedups:'generated_method_name_format-%d'`
* **Task (U)**: Move an element from one place to another.  
  **Solution**: `method:def:static add REFACTOR move:'com.example.MathHelper' 'com.example.util.MathHelper'`  
  Note: You don't need a `FROM` here if it is implied by the `move` parameters.  
  Note: This does not work for all types, it depends on the context.
