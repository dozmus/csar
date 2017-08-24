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
slc = 'slc:' [content]
mlc = 'mlc:' ['javadoc'] [content]

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
* Need more syntax for: Lambdas
* Need more syntax for: Try-catch, throw
* Need more syntax for: Computation/Storage: arithmetic, bitwise, relational, logical, assignment
* Make sure there are have no ambiguities in the grammar (one such example is being able to distinguish `{name}` from `{type}` from `{condition}`)
* A way to limit the scope of the search
* Cannot search for multiple elements at once, a top-level 'or' operator would address this.

## Use Cases
The use-cases will detail why and how the end users might use the tool.
If changes to the syntax are recommended, they will be iteratively built upon from use-case to use-case.

### Search
#### Fundamental
* **Task**: Remove all use of a deprecated class. Suppose this is replacing `Calendar` with `LocalDateTime`.  
  **Solution**: This is not something the tool aims to completely address, but it can find all usages with `class:use:Calendar` to aid the process.
* **Task**: Find all use of an interface called `HttpProvider`.  
  **Solution**: Not supported yet. The class rule can be modified to accommodate as follows: `class-modifiers = [' abstract' | ' interface'] [' strictfp'] [' anonymous'] [' inner']`.  
  Then, the solution would be `class:use:interface HttpProvider`.
* **Task**: Find all implementations of a method in an abstract class or interface, called `SuperClass`.  
  **Solution**: Not supported yet. The method rule can be modified to accommodate as follows: `method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers ['overridden'] [type] name [parameters] ['super' super-classes]`.  
  Then, the solution would be `method:def:overridden * super(SuperClass)`.
* **Task**: Find all use of a method called `add`. This might be to: ensure pending changes have no negative effects, change its signature manually or split the method into two.  
  **Solution**: `method:use:add`.  
  It is possible to also apply refinements such as: `method:u:add(2)`, `method:u:add(int, int)`, and `method:u:public static int add(int a, int b)`.
* **Task**: Find the definition of a method whose name starts with `check` to inspect it or change its signature.  
  **Solution**: `method:def:check*`, or more compactly as `fn:d:check*`.  
* **Task**: Find all use of a method called `add` whose signature states that it throws an exception called `ArithmeticException`.  
  **Solution**: Not supported yet. The method rule can be modified to accommodate as follows: `method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers ['overridden'] [type] name [parameters] ['throws ' type-list] ['super' super-classes]`.  
  Then, the solution would be `method:use:add throws ArithmeticException`.

#### Variables
* **Task**: Find all use of an instance variable (field). This might be to ensure pending changes have no negative effects.  
  **Solution**: `instance:u:result`, or refine it further to `instance:u:int result`.  
  Note: This is analogous to searching for parameters and local variables.
* **Task**: Find all variables in a class called `MyClass`.  
  **Solution**: Not supported yet. A general `variable` could be introduced rule and refinements general to all variable types allowed.

#### Control Flow
* **Task**: Remove a language feature to reduce the version requirements of a program. Suppose the initial target of this process are String switch statements (introduced in Java 1.7).  
  **Solution**: Find all switch statements with `switch` and then refine it by specifying the switch statement's argument, e.g. `switch person.getFullName()`.  
  It is not possible to search by a switch statement's argument type though. The grammar could be adapted to support it, e.g. `switch = 'switch' [' ' (expr | type)]`.  
  Its argument would be ambiguous though, so the rule will be modified to be more clear: `switch = 'switch'['(' expr ')' | ':' type]`.  
  This would lead to expressions such as `switch(person.getFullName())` and `switch:String`.
* **Task**: Find all implementations of an abstract class or interface, called `SuperClass`.  
  **Solution**: Not supported yet. The class rule can be modified as follows: `class = ('class' | 'cls' | 'c') common-modifiers class-modifiers (name | 'super') [super-classes]`.  
  This would lead to: `class:use:super(SuperClass)`.
* **Task**: Find all lambda expressions in a class called `MyClass`.  
  **Solution**: Not supported yet. A general `lambda` rule could be introduced and then introduce refinements as necessary.  
  Limiting the scope of the search is a problem which will be addressed in the next version of the query language.
* **Task**: Find all for-each statements with a specified iterated collection's type.  
  **Solution**: Not supported yet (the rule present is a placeholder). The for-each rule will be modified as follows: `foreach = 'foreach' [':' type]`, where `type` would match its subtypes as well.
* **Task**: Improve code by removing the highly stigmatised `goto` statement.  
  **Solution**: Not supported yet. A general `goto` rule could be introduced.
* **Task**: Find all if statements with a certain expression, suppose `value() == 3`.  
  **Solution**: `if value() == 3` obtained by refining the `if` rule.  
  This rule is dissimilar from the revised one for `switch`, so changing it to the following will maintain consistency in the rules: `'if' ['(' expr ')']`.  
  Note: This inconsistency applies to `while` and `dowhile` statements, and they will be modified in the same way.
* **Task**: Find all for-loops.  
  **Solution**: This is supported but quite complex. A general `for` rule will be introduced instead.
* **Task**: Find all ternary statements.  
  **Solution**: This is supported but quite complex. A general `ternary` rule will be introduced instead.
* **Task**: Find all synchronization blocks, to examine the correctness of a multi-threaded program.  
  **Solution**: Not supported yet. A general `synchronized` rule could be introduced and then allow refinements by argument, whether variable name or type (like the newly formulated rule for `switch`).  
  So, `synchronized = 'synchronized'['(' expr ')' | ':' type]` which would result in the solution being `synchronized`.

#### Comments
* **Task**: Find all 'TODO' comments (i.e. comments starting with the word `TODO`). This might be because: they are to be collated in bug tracking software or to find one to assign to a team member.  
  **Solution**: `slc:TODO *`.
* **Task**: Find all multi-line comments which are javadocs for a project-wide rewriting of them.  
  **Solution**: `mlc:javadoc`.

### Refactor
This version of the query language has no syntax to represent refactors.
A possible solution to this is to adopt the following rule: `csarquery = 'SELECT' query 'REFACTOR' refactor` and expand upon the `refactor` rule in due time.
