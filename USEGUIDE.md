Use Guide
========

# General
If you want accurate results from csar you should ensure that your source code is correct.
csar currently only supports Java 8, `package-info.java` files introduced in Java 9 will be ignored for partial
compatibility.
Parsing source code with invalid syntax will lead to an error occurring and it being skipped.

# Narrowing Domain in VCS Repositories
csar will detect and narrow its domain in supported VCS repositories (git, svn, and hg), this can be disabled using
the `--narrow-search`.  
This requires their pure CLI frontends to be installed otherwise an error will be printed.  
This is the following if a suitable git frontend is not installed: `Error running git ls-files: git is not recognized`.

# Plugins
See Report section 'High-level Structure'.

# Csar Query Language
Each csar query is comprised of four parts, but only the first is required:
* searchTarget - The element to select.
* containsQuery - What searchTarget should contain within it. (unsupported)
* fromTarget - Where searchTarget should be found (specify comma separated file names without extensions).
* refactorDescriptor - The transformation to apply to searchTarget. (unsupported)

Which are combined in the following way: `SELECT {searchTarget} FROM {fromTarget}`.

# Searching
Run csar with an appropriate csar query to find the language element(s) you want.
If no error occurs a list of search results will be displayed.

e.g. `SELECT method:def:add(int,int)` will find all method definitions with two integer parameters.

## CLI Return Codes
| Return Code | Description |
|---|---|
| 0 | Successful execution. |
| 1 | Error parsing CLI arguments. |
| 2 | Error parsing csar query. |
| 3 | Error parsing code files. |
| 4 | Error searching code files. |
| 5 | Error initializing csar. |
| 6 | Error post-processing code files. |
| 7 | Error formatting search results. |
