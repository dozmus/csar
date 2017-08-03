Useful Material
========

# Design
## Related Works
* [Ge X, Shepherd D, Damevski K, Murphy-Hill E. "Design and evaluation of a multi-recommendation system for local code search." International Journal of Computer Applications 138.6 (2016): 9-13.](http://www.sciencedirect.com.ezproxy.library.qmul.ac.uk/science/article/pii/S1045926X16300970?_rdoc=1&_fmt=high&_origin=gateway&_docanchor=&md5=b8429449ccfc9c30159a5f9aeaa92ffb&ccp=y)  
  This work is regarding plain-text code search in local projects and includes various ideas for optimisations.
* [Wang S, Lo D, Jiang L. "AutoQuery: Automatic Construction of Dependency Queries for Code Search." Automated Software Engineering 23.3 (2016): 393-425.](https://link-springer-com.ezproxy.library.qmul.ac.uk/article/10.1007%2Fs10515-014-0170-2)  
  This work is regarding dependence-based code searching. They have also devised a query language to make producing Program Dependency Graphs easier.  
* [Shepherd D, Damevski K, Ropski B, Fritz T. "Sando: an extensible local code search framework." Proceedings of the ACM SIGSOFT 20th International Symposium on the foundations of software engineering (2012).](http://dl.acm.org.ezproxy.library.qmul.ac.uk/citation.cfm?id=2393612)  
  This work is regarding an extensible plain-text code search in local projects.
* There are also tons of material on code search engines, ranking code samples and finding code from textual descriptions, if the need arises.

## Related Programs
* [grep](https://en.wikipedia.org/wiki/Grep)  
  A command-line utility for searching plain-text data against a regular expression.
* [ack](https://beyondgrep.com/)  
  A command-line utility for searching plain-text data against a regular expression - essentially a better grep.
* [ag (aka the silver searcher)](https://github.com/ggreer/the_silver_searcher)  
  A command-line utility for searching plain-text data against a regular expression - essentially a better ack.

Ag uses ignore files to narrow its search domain and multi-threading. Ack and ag search the entire project by default. I will do this as well.  
None of these however identify language elements, they are just 'better' versions of grep.

## Query Language Analysis
### AutoQuery
Their queries are broken up into the following groups: program element types (variable, function, etc.) and identifier (if applicable), program element descriptions (contains, ofType, atLine, etc.), relation descriptions (depends on, etc.) and finally targets.  
Each group can have 0 or more pieces of information within it, so it is descriptive.  
Its language isn't natural (w.r.t. english) and is very mechanical. I like how you can specify target file, line number, types, etc. and the fundamental control flow and language elements it handles. It is not very in-depth though (i.e. omits searching for try-catch blocks, anonymous methods/classes) which hinders its usefulness.

### [AspectJ](https://eclipse.org/aspectj/doc/next/progguide/starting-aspectj.html)
Their queries are almost exactly that of Java. Examples below:
* `call(void Point.setX(int)) || call(void Point.setY(int))`
* `call(void Figure.make*(..))`
* `call(public * Figure.* (..))`

I like how you can combine queries together with and, or and not.  
I like the use of `*` as a wildcard, furthermore it should also support `?` as a wildcard (matching only 1 arbitrary character).  
The idea of creating aliases for multiple queries is one I am also looking into, to provide a way of code standards verification.  
I like the way method parameters are defined.  
One problem is my tool will not be restricted to Java, so adopting a Java-like syntax will confuse/annoy some programmers.  
The syntax also fails for dynamically-typed languages where types are not explicitly defined, so a method with signature: `my_method(int)` would be invalid in it (or require lots of processing!).

### [Infer](https://github.com/facebook/infer)
Infer has developed a language called [AL](https://code.facebook.com/posts/277643589367408/) which it uses to define templates corresponding to code stink. Example below:  
```
DEFINE-CHECKER STRONG_DELEGATE_WARNING = {
    
    LET name_contains_delegate =     
        declaration_has_name(REGEXP("[dD]elegate"));
      
    LET name_does_not_contain_queue =
        NOT declaration_has_name(REGEXP("[qQ]ueue"));
    
    SET report_when =
        WHEN
           name_contains_delegate 
           AND name_does_not_contain_queue 
           AND is_strong_property()
        HOLDS-IN-NODE ObjCPropertyDecl;
    
    SET message = "Property or ivar %decl_name% declared strong";
    SET suggestion = "In general delegates should be declared weak or assign";
};
```

It is very descriptive and natural, I love this. It is also SQL-esque.  
I like how you can define strings as regex patterns, this is a powerful feature.  
I think the language is quite verbose and doesn't fit with how csar's competitors perform (one line queries), although it could have a place in batch query processing (particularly declarations and response messages).  

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

## Logging Framework
* [slf4j](https://www.slf4j.org/)

This is licensed under MIT License.

## Unit Testing
* [JUnit](http://junit.org/junit4/)
* [Mockito](http://site.mockito.org/)

These are licensed under the Eclipse Public License 1.0 and MIT License respectively.

## Command-line Parsing
* [Commons-CLI](https://commons.apache.org/proper/commons-cli/index.html)
* [JCommander](http://jcommander.org/)
* [JOpt Simple](https://pholser.github.io/jopt-simple/index.html)

My goal is to store command-line options and values into a `CsarContext` class and pass this around the program.
This would be easiest with JCommander which uses reflection to set values in an object at run-time. It is licensed under the Apache License 2.0 which is acceptable to me.

## Language Parsing
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
