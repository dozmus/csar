# Query Language (Draft #1.2)
## Syntax ([E-BNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form))
```
query = element [('CONTAINS' | 'contains') ['not'] element {('and' | 'or') ['not'] element}]
element = class | method | variable | controlflow | comment

class = ('class' | 'cls' | 'c') common-modifiers class-modifiers name [super-classes]
method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers [type] name [parameters]

variable = instance | local | param
instance = ('instance' | 'i' | 'ivar' | 'field' | 'fvar') common-modifiers instance-modifiers [type] name
local = ('local' | 'l' | 'lvar') ['final'] [type] name
param = ('param' | 'p' | 'pvar') ['final'] [type] name

controlflow = if | switch | while | dowhile | for | foreach | ternary
if = 'if' [' ' expr]
switch = 'switch' [' ' expr]
while = 'while' [' ' expr]
dowhile = 'dowhile' [' ' expr]
for = 'for' [' ' expr ',' expr ',' expr]
foreach = 'foreach' [' ' expr]
ternary = 'ternary' [' ' expr ',' expr ',' expr]

comment = slc | mlc
slc = 'slc' [' ' content]
mlc = 'mlc' [' javadoc'] [' ' content]

common-modifiers = ':' (('def' | 'd' | 'use' | 'u') ':') ['public' | 'private' | 'protected' | 'none'] ['static'] ['final']
class-modifiers = [' abstract'] [' strictfp'] [' anonymous'] [' inner']
super-classes = '(' type-list ')'
parameters = '(' (number | type-list | name-list | named-type-list) ')'
instance-modifiers = [' transient' | ' volatile']

type = word
content = word
name = word
expr = word
word = {alphanumeric}
type-list = type {',' type}
name-list = name {',' name}
named-type-list = type ' ' name {',' type ' ' name}
alphanumeric = (letter | digit | '*' | '?' | '_' | '.' | ...)
number = {digit}
```

## Examples
* `class:use:MathHelper` or `cls:u:org.mt.MathHelper` or `cls:u:MathHelper(Helper)`
* `function:use:add(2)` or `fn:u:add(int, int)` or `fn:u:add(a, b)` or `fn:u:add(int a, int b)`
* `instance:def:transient int result` or `instance:d:result`

## Problems
* Need more syntax for:
  * A way of distinguishing `{name}` from `{type}` from `{condition}`
  * Lambdas
  * Try-catch, throw
  * Computation/Storage: arithmetic, bitwise, relational, logical, assignment
  * Encapsulating class name
* Cannot search for multiple elements at once, a top-level 'or' operator would address this.

## Use Cases
### Search
* I want to remove all usage of a deprecated class. Suppose I want to replace `Calendar` with `LocalDateTime`. This is not trivial, but a good starting point is finding all the usages.  
  `class:use:Calendar`
* I want to find all TODO comments. This might be because: I want to collate them in bug tracking software or find one to fix.
  I can find all single-line comments with `slc` and then refine it further with `slc TODO *`.
* I want to find all usages of a method called `add`. This might be to: ensure my changes have no negative effects, change its signature or split the method into two.  
  I can find the usages with `method:use:add`. Refining this query is described in the next use-case.
* I want to find the definition of a method called `add` to inspect it or change its signature.  
  I can find the definition, as follows: `method:def:add`, or more compactly with `fn:d:add`.  
  I can also apply refinements such as: `fn:d:add(2)` or `fn:d:add(int, int)` or even `fn:d:public static int add(int a, int b)`.
* I want to find all usages of an instance variable (field). This might be to ensure my changes have no negative effects.  
  I can search for usages with `instance:u:result` or `instance:u:int result`.
* I want to remove a language feature to reduce the version requirements of my program. Suppose we target String switch statements (Java 1.7).  
  I can find all switch statements with `switch` and refine it further by specifying the switch statement's argument, e.g. `switch person.getFullName()`.  
  This is not supported if we want to search by the type of their argument, the grammar could be adapted to support it, e.g. `switch = 'switch' [' ' (expr | type)]`. Its argument would be ambiguous though, so we could change the rule to be more clear: `switch = 'switch'['(' expr ')' | ':' type]`.  
  This would lead to expressions such as `switch(person.getFullName())` and `switch:String`.
* I want to find all implementations of an abstract class or interface, called `SuperClass`.  
  This is not supported yet. One solution is to extend the class rule as follows: `class = ('class' | 'cls' | 'c') common-modifiers class-modifiers (name | 'super') [super-classes]`.  
  This would lead to: `class:use:super(SuperClass)`.
* I want to find all implementations of a method in an abstract class or interface, called `SuperClass`.  
  This is not supported yet. One solution is to extend the method rule as follows: `method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers ['overridden'] [type] name [parameters]`.
* I want to find usages of a method called `add` which throw an exception called `ArithmeticException`.  
  This is not supported yet. One solution is to extend the method rule as follows (taken from the above use case): `method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers ['overridden'] [type] name [parameters] ['throws ' type-list]`
* I want to find all lambda expressions in a class called `MyClass`.  
  This is not supported yet. We could begin by creating a general `lambda` rule and then refine it further as necessary.
* I want to find all synchronization blocks, to examine the correctness of my multi-threaded program.  
  This is not supported yet. We could introduce a general `synchronized` rule and then allow refinements by argument, whether variable name or type (like the newly formulated rule for `switch`).
* I want to find all for-each statements with a specified iterated collection's type.  
  This is not supported yet (the rule was a placeholder). We could introduce a refinement to the foreach rule as follows: `foreach = 'foreach' [':' type]`, where `type` would match its subtypes as well.
* We might want to improve our code by removing the highly stigmatised `goto` statements.  
  This is not supported yet. We could introduce a general `goto` rule.

### Refactor
This version of the query language has no syntax to represent refactors.
A possible solution to this is to adopt the following rule: `mainquery = 'SELECT' query 'REFACTOR' refactor` and expand upon the `refactor` rule in due time.
