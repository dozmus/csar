# Query Language (Draft #1.2)
## Syntax ([E-BNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form))
```
query = element [('CONTAINS' | 'contains') ['not'] element {('and' | 'or') ['not'] element}]
element = class | method | variable | switch | if | for | foreach | while | dowhile | ternary | slc | mlc

class = ('class' | 'cls' | 'c') common-modifiers class-modifiers name [super-classes]
method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers [type] name [parameters]
variable = instance | local | param
instance = ('instance' | 'i' | 'ivar' | 'field' | 'fvar') common-modifiers instance-modifiers [type] name
local = ('local' | 'l' | 'lvar') ['final'] [type] name
param = ('param' | 'p' | 'pvar') ['final'] [type] name

if = 'if' [' ' expr]
switch = 'switch' [' ' expr]
while = 'while' [' ' expr]
dowhile = 'dowhile' [' ' expr]
for = 'for' [' ' expr ',' expr ',' expr]
foreach = 'foreach' [' ' expr]
ternary = 'ternary' [' ' expr ',' expr ',' expr]

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
* I want to remove usage of a deprecated class. Suppose I want to replace `Calendar` with `LocalDateTime`. This is a complicated procedure, but a good starting point is finding all usages of the deprecated class.  
  I can search for usages with `class:use:Calendar`.
* I want to find all TODO comments. This might be because: I want to collate them in a bug tracking software or I want to find one to fix.  
  I can find all single-line comments with `slc` and then refine it further with `slc TODO *`.
* I want to find all usages of a method. This might be to: ensure my changes have no negative effects, understand the system, change its signature or split the method into two.  
  I can search for usages with `fn:u:add`. Refining this query is described in the next use-case.
* I want to find the definition of a method to see what it does.  
  I can find the method by name, as follows: `method:def:add`, or more compactly with `fn:d:add`.  
  I can also apply refinements such as: `fn:d:add(2)` or `fn:d:add(int, int)` or even `fn:d:public static int add(int a, int b)`.  
* I want to find all usages of an instance variable (aka field). This might be to: ensure my changes have no negative effects or better understand the system.  
  I can search for usages with `instance:result` or `instance:int result`.
* I want to use a new language feature, say streams (Java 1.8) or String switch statements (Java 1.7). I need to be able to find these elements and address them.  
  I can find all switch statements with `switch` and refine it further by specifying the switch statement's argument, e.g. `switch person.getFullName()`.  
  I cannot find them by the type of their argument, the grammar could be adapted to support it, e.g. `'switch' [' ' (expr | type)]`. Its argument would be ambiguous though, so we could change the rule to be more clear: `'switch'['(' expr ')' | ':' type]`.  
  This would lead to expressions such as `switch(person.getFullName())` and `switch:String` - fully addressing our concerns.  
  I have no possible resolutions for lambdas yet, I could add them as a general element for now, named `lambda` and refine the rule at a later date.

### Refactor
This version of the query language has no syntax to represent refactors.
