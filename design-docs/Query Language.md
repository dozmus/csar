# Query Language

## Draft #1
### Syntax
| Target Element | Query | Notes |
|---|---|---|
| class | `cls:{d|u}:{name}` |  |
| class | `cls:{d|u}:{name}({superclasses})` | `{superclasses}` are the names of the classes/interfaces this one extends (comma-separated if > 1). |
| method | `fn:{d|u}:{name}` | `{name}` is optional, and if omitted you can also omit `{d|u}`. |
| method | `fn:{d|u}:{name}({params})` |  |
| method | `fn:{d|u}:{type} {name}({params})` | `{type}` is the return type of the method. |
| variable (field, local & param) | `var:{d|u}:{name}` | `{name}` is optional, and if omitted you can also omit `{d|u}`. |
| variable (field, local & param) | `var:{d|u}:{type} {name}` | `{name}` is optional, and if omitted you can also omit `{d|u}`. |
| variable (field) | `field:{d|u}:{name}` |  |
| variable (field) | `field:{d|u}:{type} {name}` | `{type}` is the type of the variable. |
| variable (local) | `local:{d|u}:{name}` |  |
| variable (local) | `local:{d|u}:{type} {name}` |  |
| variable (param) | `param:{d|u}:{name}` |  |
| variable (param) | `param:{d|u}:{type} {name}` |  |
| for-loop | `for` | All matches. |
| while | `while` | All matches. |
| while | `while:{condition}` |  |
| if | `if` | All matches. |
| if | `if:{condition}` |  |
| do-while | `do-while` | All matches. |
| do-while | `do-while:{condition}` |  |
| switch | `switch` | All matches. |
| switch | `switch:{condition}` |  |
| switch | `switch:{type}` | switch with parameter of the given `{type}` |

Notes:
* `{def}` indicates a definition (aka declaration)
* `{use}` indicates a usage
* `{d|u}` indicates either `{def}` or `{use}`
* `{params}` is one of:
  * An integer - a count of the parameters
  * A list of types of the parameters
  * A list of identifiers of the parameters
  * Omitted - meaning no restriction on parameters

### Examples
* `cls:use:MathHelper` or `cls:u:org.mt.MathHelper`
* `fn:use:add(2)` or `fn:u:add(int, int)` or `fn:u:add(a, b)`
* `field:def:result`

### Problems
* Allow fully qualified names for types and some names (side-note)
* Need more syntax for:
  * A way of distinguishing `{name}` from `{type}` from `{condition}`
  * Verbose - shortcuts might be useful i.e. `fn -> fn:use` or `fn -> fn:def`
  * Need to be able to refine `for` queries
  * Comments (single, multi-line)
  * Ternary statement
  * Lambdas
  * Try-catch, throw
  * Anonymous classes/methods
  * Access modifiers
  * Computation/Storage: arithmetic, bitwise, relational, logical, assignment
  * Encapsulating class name

## Draft #1.1
### Syntax
| Target Element | Query | Notes |
|---|---|---|
| class | `class:{modifiers} {name}({superclasses})?` | `{superclasses}` are the names of the classes/interfaces this one extends (comma-separated if > 1). |
| method | `function:{modifiers} (overriden|abstract)? {name}({params})?` | `{type}` is the return type of the method. |
| variable (field, local & param) | `var:{modifiers} {type}? {name}?` | `{type}` is the type of the variable. |
| variable (field) | `field:{modifiers} {type}? {name}?` | " |
| variable (local) | `local:{modifiers} {type}? {name}?` | " |
| variable (method parameter) | `param:{modifiers} {type}? {name}?` | " |
| single-line comment | `slc:(class|function|var)?` | `(class|function|var)?` determines where to look for the comment. |
| multi-line comment | `mlc:(class|function|var)?` | " |
| ternary | `ternary` | All matches. |
| for-loop | `for` | All matches. |
| while | `while:{condition}?` |  |
| if | `if:{condition}?` |  |
| do-while | `do-while:{condition}?` |  |
| switch | `switch:({condition}|{name})?` | switch with parameter of the given `{name}` |

Notes:
* `{modifiers} := (definition|usage):(a|i):(public|private|protected)? (static)? (final)?` where `a` is for anonymous classes/methods/etc and `i` is for inner classes/methods/etc.
* `{params} := [0-9]+ | {type}(, {type})+ | {name}(, {name})+`
* `{name}` is a string sequence with no spaces. It can use the `*` and `?` regex flags.
  It can also narrow down to the parent class of element, e.g. `MyClass#elementName` or `com.example.MyClass#elementName`.
* Some of this syntax is RegEx-like, `{element}` denote other rules and I do not use many nested brackets and question marks for brevity.

### Syntactic Sugar
* `c | cls := class`
* `f | fn | func := function`
* `v := var`
* `fld | fvar | fv := field`
* `l | lvar | lv := local`
* `p := param`
* `u | use := usage`
* `d | def := definition`

### Problems
* Allow fully qualified names for types and some names (side-note).
* Use a more rigid and straight-forward method to describe the grammar.
* Need more syntax for:
  * A way of distinguishing `{name}` from `{type}` from `{condition}`
  * Need to be able to refine `for` and `ternary` queries
  * Lambdas
  * Try-catch, throw
  * Computation/Storage: arithmetic, bitwise, relational, logical, assignment
  * Encapsulating class name

## Draft #1.2
### Syntax ([E-BNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form))
```
query = element [('CONTAINS' | 'contains') ['not'] element {('and' | 'or') ['not'] element}]
element = class | method | variable | switch | if | for | foreach | while | dowhile | ternary | slc | mlc

class = ('class' | 'cls' | 'c') common-modifiers class-modifiers name super-classes
method = ('method' | 'function' | 'func' | 'fn' | 'f') common-modifiers name parameters
variable = instance | local | param
instance = ('instance' | 'i' | 'ivar' | 'field' | 'fvar') common-modifiers instance-modifiers type name
local = ('local' | 'l' | 'lvar') ['final'] type name
param = ('param' | 'p' | 'pvar') ['final'] type name

if = 'if' [' ' expr]
switch = 'switch' [' ' expr]
while = 'while' [' ' expr]
dowhile = 'dowhile' [' ' expr]
for = 'for' [' ' expr ',' expr ',' expr]
foreach = 'foreach' [' ' expr]
ternary = 'ternary' [' ' expr ',' expr ',' expr]

slc = content
mlc = ['javadoc'] content

common-modifiers = ':' (('def' | 'd' | 'use' | 'u') ':') ['public' | 'private' | 'protected' | 'none'] ['static'] ['final']
class-modifiers = [' abstract'] [' strictfp'] [' anonymous'] [' inner']
super-classes = '(' type-list ')'
parameters = '(' (number | type-list | name-list | named-type-list) ')'
instance-modifiers = [' transient' | ' volatile']

type = word
content = word
name = word
expr = word
word = alphanumeric {alphanumeric}
type-list = type {',' type}
name-list = name {',' name}
named-type-list = type ' ' name {',' type ' ' name}
alphanumeric = (letter | digit | '*' | '?' | '_' | '.' | ...)
number = {digit}
```

### Examples
* `class:use:MathHelper` or `cls:u:org.mt.MathHelper` or `cls:u:MathHelper(Helper)`
* `function:use:add(2)` or `fn:u:add(int, int)` or `fn:u:add(a, b)` or `fn:u:add(int a, int b)`
* `instance:def:transient int result` or `instance:d:result`

### Problems
* Need more syntax for:
  * A way of distinguishing `{name}` from `{type}` from `{condition}`
  * Lambdas
  * Try-catch, throw
  * Computation/Storage: arithmetic, bitwise, relational, logical, assignment
  * Encapsulating class name
