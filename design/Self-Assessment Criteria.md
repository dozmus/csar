Self-Assessment Criteria
========

| Activity | Priority | Measure |
|--|--|--|
| Code search functionality | High | A component which processes files with respect to the query language and yields some results, at a minimum we should be able to search for: definitions (class, method, variable), usages (class, method, variable) and control flow structures (if, for, while, do-while). |
| Replace/refactor functionality | High | A component which changes to the source code, at a minimum we should be able to: rename (class, method, variable) and change method signatures (i.e. parameter type, count, etc.). |
| Developing a suitable query language for code search | High | A language which can compactly, and simply describe many of the universal language components - including: classes, methods, variables, control flow structures. |
| Extensible design (for multi-language support) | High | Implement support for 1 language, and the basis to be able to add more relatively easily (preferably with a document explaining concise how). |
| Simple program interface | High | A straight forward command-line interface, with UNIX style program arguments. This should be able to output results in plain text and JSON to the shell. |
| Optimisation: Indexing files for big projects | Low | Allow the user to index large projects, in which the parsed ASTs for the code files will be stored somewhere and updated as necessary. |
| Optimisation: Multi-threading | Low | Allow the user to specify the amount of threads to use. At a minimum use this when parsing files and preferably when searching/replacing. |
| Use guide (with examples) | Low | A 400-500 word document explaining the purpose of the program and how to use it - this is not the README. |
| Sufficient research | Low | Search through a minimum of 20 articles (their abstracts) on [QMUL Library](http://library.qmul.ac.uk/) - don't need to select 20 through. |
| Consistent schedule | Low | Minimum of 5 commits a week. |
| Regular contact with advisor | Low | Minimum of 1 email update or meeting a week (until there is less to do). |
| Code style verification (by processing batch queries) | Low | Reading a set of queries from a file, executing them and displaying the results (passed or failed). At a minimum to support the Google Java Style Guide. |

Note: These minimums will decrease as more work has been done.