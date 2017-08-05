# Query Language (Draft #1.2)
## Syntax ([E-BNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form))
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
