# Query Language (Draft #1.3)
## Syntax ([E-BNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form))
```
csarquery = 'SELECT ' query [' FROM ' element] ' REFACTOR ' refactor
query = element ['CONTAINS' [' NOT'] element {(' AND' | ' OR') [' NOT'] element}]
element = class | method | variable | control-flow | comment
refactor = rename | change-parameters

// refactor
rename = 'rename:' name
change-parameters = 'changeparam:' parameters

// fundamental
class = 'class' common-modifiers class-modifiers (name | 'super') [super-classes]
method = 'method' common-modifiers ['overridden '] [type] name [parameters] ['throws ' type-list] ['super' super-classes]

// variables
variable = instance | local | param
instance = 'instance' common-modifiers instance-modifiers [type] name
local = 'local' ['final '] [type] name
param = 'param' ['final '] [type] name

// control flow
control-flow = if | switch | while | dowhile | for | foreach | ternary | synchronized
if = 'if' ['(' expr ')']
switch = 'switch' ['(' expr ')' | ':' type]
while = 'while' [' ' expr]
dowhile = 'dowhile' [' ' expr]
for = 'for'
foreach = 'foreach' [':' type]
ternary = 'ternary'
synchronized = 'synchronized' ['(' expr ')' | ':' type]

// comments
comment = slc | mlc
slc = 'slc:' [content]
mlc = 'mlc:' ['javadoc:'] [content]

// helpers
common-modifiers = ':' (('def' | 'use') ':') ['public' | 'private' | 'protected' | 'none'] ['static'] ['final']
class-modifiers = [' abstract' | ' interface'] [' strictfp'] [' anonymous'] [' inner']
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

## Implementation
The preliminary version of the software will only handle searching for classes and methods.
Later versions will gradually include other features of the query language, until it is completely supported.  
Note: This version of the grammar has omitted aliases for the language, for brevity.  
Note: The Java8PT ANTLR grammar does not parse comments, hence modification to it will be necessary.
