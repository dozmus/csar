# Query Language (Draft #1.0.1)
## Syntax
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

## Syntactic Sugar
* `c | cls := class`
* `f | fn | func := function`
* `v := var`
* `fld | fvar | fv := field`
* `l | lvar | lv := local`
* `p := param`
* `u | use := usage`
* `d | def := definition`

## Problems
* Allow fully qualified names for types and some names (side-note).
* Use a more rigid and straight-forward method to describe the grammar.
* Need more syntax for:
  * A way of distinguishing `{name}` from `{type}` from `{condition}`
  * Need to be able to refine `for` and `ternary` queries
  * Lambdas
  * Try-catch, throw
  * Computation/Storage: arithmetic, bitwise, relational, logical, assignment
  * Encapsulating class name
