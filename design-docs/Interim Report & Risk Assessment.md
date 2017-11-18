Interim Report & Risk Assessment
========

Project: csar: Query-driven Code Search and Refactoring Framework  
Author: Deniz Ozmus  
Supervisor: Michael Tautschnig

<!-- TODO before converting to PDF, make sure this is on its own page -->

# Introduction
## csar
csar aims to provide a unified framework to address code searching and refactoring. A newly devised query language called the csar query language will be used to describe searches and refactors. This creates a versatile and descriptive framework which can target any programming language without the developer needing to know the programming languages.

csar will be designed to be very flexible code-wise as well. With the proper use of subclasses, a third-party developer should be able to easily customize its behaviour to fit their specific needs. Most of its API will be publicly exposed, this way it can be embedded seamlessly into complicated build processes and other applications.

## Pre-requisite Knowledge
Definition: Code refactoring is the process of restructuring existing computer code—changing the factoring—without changing its external behaviour. Refactoring improves non-functional attributes of the software.<sup>[1](#footnote1)</sup>

Definition: Parsing, syntax analysis or syntactic analysis is the process of analysing a string of symbols, either in natural language or in computer languages, conforming to the rules of a formal grammar.<sup>[2](#footnote2)</sup>

Definition: The visitor design pattern is a way of separating an algorithm from an object structure on which it operates.<sup>[3](#footnote3)</sup>

Definition: ANTLR is a parser-generator.<sup>[4](#footnote4)</sup>

Definition: A compiler-compiler (also known as a parser-generator) is a programming tool that creates a parser, interpreter, or compiler from some form of formal description of a language and machine. The input may be a text file containing the grammar written in BNF or EBNF that defines the syntax of a programming language, and whose generated output is some source code of the parser for the programming language, although other definitions exist.<sup>[5](#footnote5)</sup>

<!-- TODO finish -->

# User Requirements
## General Requirements
* A user guide detailing how to use the tool and its query language.
* A custom query language to describe the operations to carry out.
* Parsing of code.
* Language-agnostic searching.
* Language-agnostic refactoring.
* Flexible program output.
* Custom search domains.
* High efficiency.

## Selected Requirements
The project has strict deadlines, and as such, not every feature will be fully implemented.

The query language has two versions: 1.x.x (implemented) and ≥ 2.x.x (hypothetical improvements). The prior version is simpler than the latter, for implementation purposes. Implementing an exhaustive query language which can express any language element in any language would take very long (it would be an entire project in itself), hence these are open for future improvements.

For now only java code will be parsed, the system will be designed so that other programming languages can easily be integrated into the current structure. You will just need to write a parser for them, analogously to how the java parser will be written.

Searching will be implemented to only work on methods for now. To implement it for the other language elements described in the query language would be as simple as copy-pasting the code for method searching and changing the types and getters involved. This would be a greatly time consuming and tedious task.

The program output could technically support thousands of formats, but for simplicity (and reduced binary size) it will only support two: plain text and JSON. If you require alternatives, you should be able to subclass a result formatter.

Refactoring is very complicated and time consuming, so we will only consider two operations.  
The first is changing method parameters, this involves resolving all usages of the method in question (in various contexts), modifying the method calls and its definition and ensuring no naming collisions occur.  
The second is renaming methods, this should follow from changing method parameters, since if we resolve all method usages we should be able to simply rename them all and ensure no naming collisions occur.

Efficiency will mainly be addressed by introducing multi-threading where possible, algorithmic solutions to this are currently unknown, but may become available as details of the implementation are gradually addressed.

It will also support narrowing the search domain by a `.gitignore` file for directories which are git repositories, and by custom `.csarignore` file. Support for other version control systems are a backlog task and may not be fulfilled, but will follow trivially from the git implementation.  
The git implementation will require calling the git binary with a specific argument and reading the output, which will reveal which files it is currently tracking.

## Back-logged Requirements
These requirements are non-essential, but may be addressed if there is sufficient time, or if priorities shift:

### Indexing parsed project code
* Store a `(Path, LastModified, Code)` relation somewhere, preferably in a `.csar` directory.
* The `LastModified` date is the last time that file was modified, as of when it was parsed.
  We can use this to determine which files need updating. This can be error-prone, since
  last modified dates can be spoofed, but it would offer no advantage to an attacker.
* Approach 1: Flat Files  
  Either map files to a parallel hierarchy in `.csar`, or, store them in files where their names are the hashes of the input files.
* Approach 2: Database (i.e. SQLite)  
  Store the aforementioned relation here.

### Supporting Mercurial (hg)/Subversion (svn) repositories
This would be done how the git implementation is, but the program arguments we use may be some of the following:
* Hg - `hg status --all`
* Svn - `svn list -R` or `svn status`

### Support for further searching
The process for this is described earlier, I would simply have to repeat the process for each additional element I want to support searching for.

### IntelliJ IDEA Integration
csar can be integrated into this IDE in one of two ways:
* Internally - csar can be implemented as an alternative to IDEA's structural search,
  this would enable it to carry out the following tasks:
  identifier usage finding, overridden method finding, type hierarchy resolving, refactoring,
  and semantics-based searching.  
  This requires placing our code in
  [`platform/structuralsearch/.../structuralsearch`](https://github.com/JetBrains/intellij-community/tree/master/platform/structuralsearch/source/com/intellij/structuralsearch) and creating adapters to enable our code to provide the same interface as the current IDEA ones, to ensure maximum compatibility.
* Plugin - csar can be implemented as a third-party plugin which introduces a new query field.
  This field would allow users to type csar queries and then execute them, displaying the results in a standard IDEA result window.

# Design
## Component Overview
Overview:  
CLI &rightarrow; Csar Query Parser &rightarrow; Project Code Parser &rightarrow; Code Post-processors &rightarrow; Search &rightarrow; Refactor &rightarrow; Results

CLI:  
csar receives some command-line arguments including a non-optional search query.
These determine what tasks csar will execute.

Its usage format is shown below:
```
Usage: java -jar csar.jar [options] Search query
  Options:
    --threads, -t
      Thread count (default: 1)
    --log-level
      Log level (default: INFO)
      Possible Values (most restrictive to least): ERROR, WARN, INFO, DEBUG, TRACE
    --format, -f
      Output format (default: PlainText)
      Possible Values: PlainText, JSON
    --output, -o
      Output file name
    --narrow-search
      Narrow search domain (default: true)
    --ignore-file
      Ignore file (default: .csarignore)
    --benchmark
      Print benchmarking values (default: false)
    --project-url, --url
      Print project URL
    --help, -h
      Print help information
```

These values are stored in an instance of `CsarContext` using JCommander.
JCommander will try to match the command-line arguments with fields in said object using the values in their `@Parameter` annotations.

Then, `CsarFactory` will use this instance of `CsarContext` to create a corresponding instance of `Csar`.
At this stage, a project iterator which determines files to parse is also created.

Csar Query Parser:  
The csar query in `CsarContext` will be parsed using the corresponding ANTLR4 grammar. Each csar query is comprised of four parts, but only the first is required:
 * `searchTarget` - The element to select.
 * `containsQuery` - What `searchTarget` should contain within it.
 * `fromTarget` - Where `searchTarget` should be found.
 * `refactorDescriptor` - The transformation to apply to `searchTarget`.

Project Code Parser:  
The project code will be parsed (currently only Java 8 is supported).
This is after csar query is parsed because it typically takes longer, and an invalid csar query terminates the system.
It will also perform some code validation because the Java 8 ANTLR4 grammar relies on post-processing for correctness.

Code Post-processors:  
The parsed project code is post-processed to get us information we may need for searching, which we do not possess already.
This currently includes type hierarchy resolving and overridden methods resolving.
This will also map method usages to method definitions (unimplemented).

Search:  
The parsed project code is searched using the visitor design pattern.
Each language element in each code file is visited, and if a match to the language descriptor we are looking for is found, then that element is added to the list of search results.
This will soon also handle the `FROM` and `CONTAINS` clauses from csar query (unimplemented).

Refactor:  
The parsed project code will be refactored, and changes will be written to source code files (unimplemented).

Results:  
The search and refactor results are printed.

Note: If an unrecoverable error occurs at any of the aforementioned stages, the error will be displayed in a user-friendly format and csar will terminate.
The user may be able to override this behaviour through sub-classing.

## Running Example
Suppose we invoke csar from the command-line with the following command: `java -jar csar.jar SELECT method:def:overridden parse -t 1`.

Firstly, these command-line arguments will be parsed, here we have the tasks: execute the csar query `SELECT method:def:overridden parse` and set the thread count to 1.
An instance of `Csar` will be created with these arguments and it will attempt to fulfil this task.

Secondly, the csar query will be parsed into an equivalent `CsarQuery` object.

Thirdly, the project code will be parsed and stored in a map.

Fourthly, the parsed project code will be post-processed.

Fifthly, the search will be executed and method definitions who are overridden from a super-class and have the name `parse` will be stored in a search results list.

Sixthly, this query defines no refactoring, so the refactoring step will be skipped.

Finally, csar terminates.

<!-- TODO finish -->

# Background Material
## Related Academic Works
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

## References
* Ignore files syntax - https://git-scm.com/docs/gitignore, https://github.com/EE/gitignore-to-glob/blob/master/lib/gitignore-to-glob.js
* Test cases for ignore files - https://www.atlassian.com/git/tutorials/gitignore
* Git repository integration - https://git-scm.com/docs/git-ls-files
* `CsarLexer`'s `JAVA_LETTER` rule - https://github.com/antlr/grammars-v4/blob/master/java8/Java8.g4
* Using JCommander parameters in `CsarContext` - http://jcommander.org/
* ANTLR settings in `build.gradle` - https://docs.gradle.org/4.0.1/userguide/antlr_plugin.html#sec:controlling_the_antlr_generator_process
* JaCoCo in `build.gradle` - http://www.jworks.nl/2013/06/03/jacoco-code-coverage-with-gradle/

<!-- TODO add on to as appropriate -->

# Time Plan
The work completed so far can be seen in full in the Github [commits](https://github.research.its.qmul.ac.uk/ec15116/csar/commits/master).  
A concise description is that: the command-line interface, csar query language parsing, Java code parsing, and search (partially) are completed.

* 18/12/2017 - Mapping method usages to method definitions
* 08/12/2017 - Interim report and risk assessment
* 08/01/2018 - Implement searching method usages
* 11/01/2018 - Finish searching (supporting from and contains query clauses)
* 12/02/2018 - Implement refactoring
* 01/03/2018 - Implement full language-agnosticism
* 19/03/2018 - Draft final report (begin after Implement refactoring)
* 23/04/2018 - Final report
* 25/04/2018 - Presentation slides

# Risk Assessment
| Description of risk | Description of impact | Likelihood rating | Impact rating | Preventative actions |
|--|--|--|--|--|
| Low motivation/Burn out | Overdue/incomplete work | Medium | High | Schedule project work carefully, leave time for entertainment. |
| Busy schedule/Poor time management | Overdue/incomplete work | Medium | High | Schedule carefully, try to get work done on time as necessary. |
| Advisor has busy schedule | Inadequate work quality. | Medium | Low | Take initiative as necessary, use library resources to fill in gaps in knowledge. |
| Code defects | Inadequate program functionality. | Medium | Medium | Try to fix them, if not possible then write about them in the report and leave them. |
| Code bugs | Inadequate program quality. | Medium | Low | Try to fix them, if not possible then write about them in the report and leave them. Do not pretend they do not exist. |
| Code quality | More difficult development. | Low | Low | Incremental development should help prevent this. |
| Incomplete design | Pre-requisite to implementation. | Medium | High | Schedule work on the design to keep up with the pace of the programming. |
| IDE crash | Lost work | Low | High | Save often, use version control. |

# Bibliography
* <a name="footnote1">1</a>: Wikipedia (2017) *Code refactoring - Wikipedia*. Available at: https://en.wikipedia.org/wiki/Code_refactoring (Accessed: 18 November 2017)
* <a name="footnote2">2</a>: Wikipedia (2017) *Parsing - Wikipedia*. Available at: https://en.wikipedia.org/wiki/Parsing (Accessed: 18 November 2017)
* <a name="footnote3">3</a>: Wikipedia (2017) *Visit pattern - Wikipedia*. Available at https://en.wikipedia.org/wiki/Visitor_pattern (Accessed: 18 November 2017)
* <a name="footnote4">4</a>: Wikipedia (2017) *ANTLR - Wikipedia*. Available at https://en.wikipedia.org/wiki/ANTLR (Accessed: 18 November 2017)
* <a name="footnote5">5</a>: Wikipedia (2017) *Compiler-compiler - Wikipedia*. Available at https://en.wikipedia.org/wiki/Compiler-compiler (Accessed: 18 November 2017)

<!-- TODO do i need to cite my specification, since I copy-pasted from it -->
