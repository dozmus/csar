Interim Report & Risk Assessment
========

Project: csar: Query-driven Code Search and Refactoring Framework  
Author: Deniz Ozmus  
Supervisor: Michael Tautschnig

<!-- TODO before converting to PDF, make sure this is on its own page -->

TODO write: chapter structure, chapters: bg materials, references, reqs, design so far

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
