Useful Material
========

# Design
## Related Works
* [Ge X, Shepherd D, Damevski K, Murphy-Hill E. "Design and evaluation of a multi-recommendation system for local code search." International Journal of Computer Applications 138.6 (2016): 9-13.](http://www.sciencedirect.com.ezproxy.library.qmul.ac.uk/science/article/pii/S1045926X16300970?_rdoc=1&_fmt=high&_origin=gateway&_docanchor=&md5=b8429449ccfc9c30159a5f9aeaa92ffb&ccp=y)  
  This work is regarding plain-text code search in local projects and includes various ideas for optimisations.
* [Wang S, Lo D, Jiang L. "AutoQuery: Automatic Construction of Dependency Queries for Code Search." Automated Software Engineering 23.3 (2016): 393-425.](https://link-springer-com.ezproxy.library.qmul.ac.uk/article/10.1007%2Fs10515-014-0170-2)  
  This work is regarding code searching with respect to language elements, they also devised a query language for this.
* [Shepherd D, Damevski K, Ropski B, Fritz T. "Sando: an extensible local code search framework." Proceedings of the ACM SIGSOFT 20th International Symposium on the foundations of software engineering (2012).](http://dl.acm.org.ezproxy.library.qmul.ac.uk/citation.cfm?id=2393612)  
  This work is regarding an extensible plain-text code search in local projects.
* There are also tons of material on code search engines, ranking code samples and finding code from textual descriptions, if the need arises.

## Code Style Documents
* [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* [Twitter Java Style Guide](https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/styleguide.md)

## (Partial) Java Language Elements
* function definition
* function usage
* variable definition
* variable usage
* class definition
* class usage
* internal classes, anonymous classes
* anonymous methods
* package definition
* package usage
* for loop
* for-each loop
* try-catch
* try with resources
* throw
* while loop
* do-while loop
* if stmt
* goto
* switch stmt
* ternary stmt (aka conditional)
* lambdas
* single line comment
* multi line comment
* arithmetic: + - / * % ++ --
* relational: == != > < >= <=
* bitwise: & | ^ ~ << >> >>>
* logical: && || !
* assignment: = += -= *= /= %= <<= >>= &= |= ^=

Many obtained from [Tutorials Point](https://www.tutorialspoint.com/java/java_basic_operators.htm) and [WideSkills](http://www.wideskills.com/java-tutorial/java-basic-language-elelemnts).

# Libraries
## Build Tool
* [Gradle](https://gradle.org/)

This is licensed under Apache License 2.0.

## Unit Testing
* [JUnit](http://junit.org/junit4/)
* [Mockito](http://site.mockito.org/)

These are licensed under the Eclipse Public License 1.0 and MIT License respectively.

## Command-line Parsing Libraries
* [Commons-CLI](https://commons.apache.org/proper/commons-cli/index.html)
* [JCommander](http://jcommander.org/)
* [JOpt Simple](https://pholser.github.io/jopt-simple/index.html)

My goal is to store command-line options and values into a `CsarContext` class and pass this around the program.
This would be easiest with JCommander which uses reflection to set values in an object at run-time. It is licensed under the Apache License 2.0 which is acceptable to me.

## Language Parsing Libraries
* [ANTLR4](http://www.antlr.org/)
* [Grammatica](https://grammatica.percederberg.net/)
* [JavaCC](https://github.com/javacc/javacc)
* [SableCC](http://www.sablecc.org/)
* [parboiled](https://github.com/sirthias/parboiled/wiki)
* [Coco/R](http://ssw.jku.at/Coco/)

I require a language parser-generator library which is updated frequently, well-tested and with lots of available pre-written grammars licensed liberally.

ANTLR seems acceptable, it has many [grammars](https://github.com/antlr/grammars-v4) and is licensed under BSD. It had a performance defect in its Java 1.8 grammar, but a new one was written to address it.  
Grammatica seems to lack up-to-date pre-written grammars.  
JavaCC may have many pre-written grammars but they are [not collated](https://github.com/javacc/javacc/issues/14) - this is because the web page hosting them has been deleted.  
SableCC has only three officially accepted [pre-written grammars](http://www.sablecc.org/grammars).  
parboiled lacks up-to-date pre-written grammars (there are outdated ones for [Java 1.6](https://github.com/sirthias/parboiled/wiki/Java-Parser) and [Scala 2.8](https://github.com/sirthias/parboiled/wiki/parboiled-for-Scala)).  
Coco/R seems lacks up-to-date pre-written grammars (there are outdated ones for [Java 1.4](http://ssw.jku.at/Coco/Java/Java.ATG) and [C# 3.0](http://ssw.jku.at/Coco/CS/CSharp3.atg)).  

It also appears that the build tools ant and gradle support ANTLR and JavaCC out of the box.
