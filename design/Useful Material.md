Useful Material
========

# Design
## Related Works
...

## Code Convention Documents
...

# Libraries
## Build Tool
* [Gradle](https://gradle.org/)

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

## Unit Testing Libraries
...
