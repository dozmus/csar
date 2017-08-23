# Query Language (Draft #1.2)
## Syntax ([E-BNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form))
```
query = element [('CONTAINS' | 'contains') ['not'] element {('and' | 'or') ['not'] element}]
element = class | method | variable | control-flow | comment

// fundamental
class = ('class' | 'cls' | 'c') common-modifiers class-modifiers name [super-classes]
method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers [type] name [parameters]

// variables
variable = instance | local | param
instance = ('instance' | 'i' | 'ivar' | 'field' | 'fvar') common-modifiers instance-modifiers [type] name
local = ('local' | 'l' | 'lvar') ['final'] [type] name
param = ('param' | 'p' | 'pvar') ['final'] [type] name

// control flow
control-flow = if | switch | while | dowhile | for | foreach | ternary
if = 'if' [' ' expr]
switch = 'switch' [' ' expr]
while = 'while' [' ' expr]
dowhile = 'dowhile' [' ' expr]
for = 'for' [' ' expr ',' expr ',' expr]
foreach = 'foreach' [' ' expr]
ternary = 'ternary' [' ' expr ',' expr ',' expr]

// comments
comment = slc | mlc
slc = 'slc' [' ' content]
mlc = 'mlc' [' javadoc'] [' ' content]

// helpers
common-modifiers = ':' (('def' | 'd' | 'use' | 'u') ':') ['public' | 'private' | 'protected' | 'none'] ['static'] ['final']
class-modifiers = [' abstract'] [' strictfp'] [' anonymous'] [' inner']
super-classes = '(' type-list ')'
parameters = '(' (number | type-list | name-list | named-type-list) ')'
instance-modifiers = [' transient' | ' volatile']
type-list = type {',' type}
name-list = name {',' name}
named-type-list = type ' ' name {',' type ' ' name}

// units
type = word | primitive-type
content = word
name = word
expr = word
word = {alphanumeric}
alphanumeric = (letter | digit | '*' | '?' | '_' | '.' | '(' | ')' | ...)
number = {digit}
primitive-type = 'int' | 'double' | 'float' | 'long' | 'short' | 'char' | 'byte' | 'boolean'
```

## Examples
* `class:use:MathHelper` or `cls:u:org.mt.MathHelper` or `cls:u:MathHelper(Helper)`
* `function:use:add(2)` or `fn:u:add(int, int)` or `fn:u:add(a, b)` or `fn:u:add(int a, int b)`
* `instance:def:transient int result` or `instance:d:result`

## Problems
* Need more syntax for:
  * Make sure we can distinguish `{name}` from `{type}` from `{condition}`
  * Lambdas
  * Try-catch, throw
  * Computation/Storage: arithmetic, bitwise, relational, logical, assignment
  * Encapsulating class name
* Cannot search for multiple elements at once, a top-level 'or' operator would address this.

## Use Cases
### Search
#### Fundamental
* I want to remove all usage of a deprecated class. Suppose I want to replace `Calendar` with `LocalDateTime`. This is not trivial, but a good starting point is finding all the usages.  
  `class:use:Calendar`
* I want to search for the usage of an interface called `HttpProvider`.  
  This is not supported yet. One solution is to extend the class rule as follows: `class-modifiers = [' abstract' | ' interface'] [' strictfp'] [' anonymous'] [' inner']`.
* I want to find all implementations of a method in an abstract class or interface, called `SuperClass`.  
  This is not supported yet. One solution is to extend the method rule as follows: `method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers ['overridden'] [type] name [parameters]`.
* I want to find all usages of a method called `add`. This might be to: ensure my changes have no negative effects, change its signature or split the method into two.  
  I can find the usages with `method:use:add`. Refining this query is described in the next use-case.
* I want to find the definition of a method called `add` to inspect it or change its signature.  
  I can find the definition, as follows: `method:def:add`, or more compactly with `fn:d:add`.  
  I can also apply refinements such as: `fn:d:add(2)` or `fn:d:add(int, int)` or even `fn:d:public static int add(int a, int b)`.
* I want to find usages of a method called `add` which throw an exception called `ArithmeticException`.  
  This is not supported yet. One solution is to extend the method rule as follows (taken from the above use case): `method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers ['overridden'] [type] name [parameters] ['throws ' type-list]`.

#### Variables
* I want to find all usages of an instance variable (field). This might be to ensure my changes have no negative effects.  
  I can search for usages with `instance:u:result` or `instance:u:int result`.  
  Note: This can be used for parameters and local variables too.
* I want to find all variables in a class called `MyClass`.  
  This is not supported yet. We could introduce a general `variable` rule and refine it as necessary.

#### Control Flow
* I want to remove a language feature to reduce the version requirements of my program. Suppose we target String switch statements (Java 1.7).  
  I can find all switch statements with `switch` and refine it further by specifying the switch statement's argument, e.g. `switch person.getFullName()`.  
  This is not supported if we want to search by the type of their argument, the grammar could be adapted to support it, e.g. `switch = 'switch' [' ' (expr | type)]`. Its argument would be ambiguous though, so we could change the rule to be more clear: `switch = 'switch'['(' expr ')' | ':' type]`.  
  This would lead to expressions such as `switch(person.getFullName())` and `switch:String`.
* I want to find all implementations of an abstract class or interface, called `SuperClass`.  
  This is not supported yet. One solution is to extend the class rule as follows: `class = ('class' | 'cls' | 'c') common-modifiers class-modifiers (name | 'super') [super-classes]`.  
  This would lead to: `class:use:super(SuperClass)`.
* I want to find all lambda expressions in a class called `MyClass`.  
  This is not supported yet. We could begin by creating a general `lambda` rule and then introduce refinements as necessary.
* I want to find all for-each statements with a specified iterated collection's type.  
  This is not supported yet (the rule was a placeholder). We could introduce a refinement to the foreach rule as follows: `foreach = 'foreach' [':' type]`, where `type` would match its subtypes as well.
* We might want to improve our code by removing the highly stigmatised `goto` statement.  
  This is not supported yet. We could introduce a general `goto` rule.
* I want to find all if statements with a certain expression, say `value() == 3`.  
  I could use the `if` rule and then refine it, `if value() == 3`.  
  This syntax is dissimilar than the new one for `switch`, so I will change it to the following: `'if' ['(' expr ')']`.  
  Note: The same applies to `while` and `dowhile` statements, and I will modify them in the same way.
* I want to find all for-loops.  
  I think it should remain a general `for` rule since it can be a very complex language construct.
* I want to find all ternary statements.  
  I think it should remain a general `ternary` rule since it can be a very complex language construct.
* I want to find all synchronization blocks, to examine the correctness of my multi-threaded program.  
  This is not supported yet. We could introduce a general `synchronized` rule and then allow refinements by argument, whether variable name or type (like the newly formulated rule for `switch`).

#### Comments
* I want to find all TODO comments. This might be because: I want to collate them in bug tracking software or find one to fix.
  I can find all single-line comments with `slc` and then refine it further with `slc TODO *`.

### Refactor
This version of the query language has no syntax to represent refactors.
A possible solution to this is to adopt the following rule: `csarquery = 'SELECT' query 'REFACTOR' refactor` and expand upon the `refactor` rule in due time.
