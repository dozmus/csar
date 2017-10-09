Specification and Time Plan
========

# Project Title
csar: Query-driven Code Search and Refactoring Framework

# Problem
There is a lack of tools which provide flexible searching for code, most offer plain-text and regular expression comparisons. This yields inaccurate results due to the rigidness of these comparison types.

Furthermore, there is a lack of tools which combine refactoring with searching. These two operations are closely intertwined and creating separate software solutions for them makes it harder for developers to use them effectively. This is because they need to learn how to use two pieces of software instead of one.

CSAR aims to provide a versatile and unified framework to address both these issues. A newly devised query language will be used to describe searches and refactors in terms of descriptions. This creates a versatile, descriptive framework which can target any programming language without the developer needing to know about the programming language itself.

CSAR will be developed in such a way that, it is very flexible in its programming as well. With the proper use of subclasses, a third-party developer can easily customize its behavior to fit their specific needs. Most of its internal API will be publicly exposed, this way it can be embedded seamlessly into complicated build processes and other applications.

# User Requirements
## General Requirements
* A user guide detailing how to use the tool and its query language.
* Language-agnostic searching.
* Language-agnostic refactoring.
* Custom search destinations.
* High efficiency.

## Selected Requirements

The project has a restricted window for completion, as such, not every intended feature will be fully completed.

The query language has two versions: 1.x.x (implemented) and ≥ 2.x.x (hypothetical improvements). The prior version is simpler than the latter, for implementation purposes. Implementing an exhaustive query language which can express any language element in any language would take very long (it would be an entire project in itself), hence these are open for future improvements.

Searching will be implemented to only work on methods for now. To implement it for the other language elements it would be as simple as copy-pasting the code for methods and changing the types and getters involved. Though this would be a greatly time consuming and tedious task.

Refactoring will be implemented to only work on methods for renaming and changing parameters. Implementing renaming for other language elements would be done with copy-pasting, as mentioned before. Changing parameters does not apply to any other language element, so that doesn’t have to be considered.

# User Interaction
The user will run the program from the command-line, as they would any other Java JAR: `java -jar csar.jar [options]`. A preliminary list of options is listed below:
* The search query, this does not correspond to an option flag, since it is required.
* `--threads` – Thread count.
* `--log-level` – Log level (for debugging purposes).
* `--format` – Output format.
* `--narrow-search` – Narrow search domain automatically.
* `--ignore-file` – The ignore file to use, to further narrow the search domain.
* `--project-url` – The project URL.
* `--help` – CLI help information.

i.e. `SELECT method:def:add --threads 4` – The first part is the search query, and the latter a `--threads` argument.

# Programming Concepts/Techniques Used
* Dependency Injection (DI) - passing the dependencies of an object to it explicitly.  
  This makes it easier to test objects, and makes their dependencies more visible.
  I will mostly be utilizing the variant of this where we inject dependencies through the constructor, to keep the design very clear and simple (one point of injection).
* Multi-threading - distributing tasks to multiple execution threads, to increase throughput.  
  This will give us a huge performance boost, especially for very large code bases, with relatively little effort.
* Visitor design pattern - a method of interacting with a complicated, nested object.  
  This is used in the ANTLR code parsers, to generate POJO representing the parsed text.
  This is also used in the search algorithm for comparisons.
* Factory design pattern - an external object which creates a complicated object.  
  This is used to generate the complicated arguments some of our objects may require, in particular because of DI.
* Object-Oriented Programming principles - model the system as interactions amongst a set of objects (this is the consensus approach for Java development).  
  The most used are polymorphism and the Liskov substitution principle throughout the project, for extensibility.

TODO add onto this as appropriate

# Technologies Used
CSAR will be programmed in Java 8, this is because I am very well-versed in it, it bought forth a powerful directory walking API, it has lambda support, and it is cross-platform.

There are many different language parsers available, but the most convenient to use (due to documentation, support and pre-written grammars) appears to be ANTLR4.

It will use JUnit for unit testing because it is industry standard and thus supported by the most popular IDEs. Mockito/PowerMockito may also be used for advanced testing if necessary.

SLF4J will be used for logging because it is lightweight, modular and industry standard.

JCommander will be used for command-line interface (CLI) parsing, because it uses annotations to make the process very simple and short in terms of line count.

Gradle will be used for configuration management because it has a powerful ANTLR plugin and is simple to use. Git will be used for revision control and IntelliJ will be my choice of IDE because they are objectively superior to the rest.

# Background Material
# Related Academic Works
* [Ge X, Shepherd D, Damevski K, Murphy-Hill E. "Design and evaluation of a multi-recommendation system for local code search." International Journal of Computer Applications 138.6 (2016): 9-13.](http://www.sciencedirect.com.ezproxy.library.qmul.ac.uk/science/article/pii/S1045926X16300970?_rdoc=1&_fmt=high&_origin=gateway&_docanchor=&md5=b8429449ccfc9c30159a5f9aeaa92ffb&ccp=y)  
  This work is regarding plain-text code search in local projects and includes various ideas for optimisations.
* [Wang S, Lo D, Jiang L. "AutoQuery: Automatic Construction of Dependency Queries for Code Search." Automated Software Engineering 23.3 (2016): 393-425.](https://link-springer-com.ezproxy.library.qmul.ac.uk/article/10.1007%2Fs10515-014-0170-2)  
  This work is regarding dependence-based code searching. They have also devised a query language to make producing Program Dependency Graphs easier.  
* [Shepherd D, Damevski K, Ropski B, Fritz T. "Sando: an extensible local code search framework." Proceedings of the ACM SIGSOFT 20th International Symposium on the foundations of software engineering (2012).](http://dl.acm.org.ezproxy.library.qmul.ac.uk/citation.cfm?id=2393612)  
  This work is regarding an extensible plain-text code search in local projects.

There are also a lot of material on code search engines, ranking code samples and text matching, but these are largely unrelated to the task at hand.

## Related Programs
* [grep](https://en.wikipedia.org/wiki/Grep)  
  A command-line utility for searching plain-text data against a regular expression.
* [ack](https://beyondgrep.com/)  
  A command-line utility for searching plain-text data against a regular expression - essentially a better grep.
* [ag (aka the silver searcher)](https://github.com/ggreer/the_silver_searcher)  
  A command-line utility for searching plain-text data against a regular expression - essentially a better ack.

## Query Language Analysis
The following languages do not necessarily address my problem but they may influence the development of my own query language.

### AutoQuery
Its queries are broken up into the following groups: program element types (variable, function, etc.) and identifier (if applicable), program element descriptions (contains, ofType, atLine, etc.), relation descriptions (depends on, etc.) and finally targets. Each group can have 0 or more pieces of information within it, so it is descriptive.

Its language is unnatural (w.r.t. English) and is very rigid. You can specify file, line number, types, and various elementary language elements (classes, methods, control flow). It is not very expressive: it cannot represent try-catch blocks, anonymous methods/classes, and a long list of such constructs.

### [AspectJ](https://eclipse.org/aspectj/doc/next/progguide/starting-aspectj.html)
AspectJ has developed a language which it uses to address the problems presented by aspect-oriented programming.  

Their queries have a syntax that closely resembles that of Java. Examples below:
* `call(void Point.setX(int)) || call(void Point.setY(int))`
* `call(void Figure.make*(..))`
* `call(public * Figure.* (..))`

You can restrict the domain of queries with logical operators (and, or and not). It has limited wildcards (`*`, `..` which serves as `*` specially for method parameters, but it lacks `?`).

You can define a query with an alias, which helps address the Don't Repeat Yourself principle, and thus verbosity. I can also use this to provide coding standards verification (i.e. define queries and run them against the project, with matches corresponding errors in standards). You can define method parameters as a type list or a named-type list which is flexible and expressive.

One issue is csar aims to be language-agnostic, so adopting a Java-like syntax will be unintuitive for non-Java programmers.

The syntax fails for dynamically-typed languages where types are not explicitly defined, so a method with signature: `my_method(int)` would be invalid (or require additional processing to make work). A solution to this is accepting variable name lists.

### [Infer](https://github.com/facebook/infer)
Infer has developed a language called [AL](https://code.facebook.com/posts/277643589367408/) which it uses to define templates corresponding to code stink. Example below:  
```
DEFINE-CHECKER STRONG_DELEGATE_WARNING = {

    LET name_contains_delegate = declaration_has_name(REGEXP("[dD]elegate"));
    LET name_does_not_contain_queue = NOT declaration_has_name(REGEXP("[qQ]ueue"));

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

It is very descriptive (allows compositions with `AND`, `NOT`, `WHEN` etc.) and intuitive (like SQL). You can define strings as regex patterns, this is a powerful feature. The language is verbose and thus does not resonate with csar's competitors, which use single line queries. However, declarations and response messages can be useful for creating complex tools with csar (i.e. code convention checks).

# Time Plan
TODO write about the following:
* Project Deadlines
  * 13 October - Title
  * 30 October - Specification & Time Plan
  * 8 December - Interim Report & Risk Assessment
  * 19 March - Draft final report
  * 23 April - Final report
  * 25 April - Presentation slides
  * 30 April - 11 May - Demonstration & Presentation
* Features (Left) - Ask MT If I should include the ones I've already done
  * Search
  * Refactor
