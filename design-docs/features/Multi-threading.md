Multi-threading
========

# File Processing (Parsing, Search & Refactoring)
__Problem__: Distribute and parse _n_ source files, search them and then apply refactors to them on _k_ different threads.

__Proposed Solution__:  
* Create thread pool with a fixed size (from cli)
* Submit tasks to:
  * Parse source files
  * Apply search
  * If search is successful apply refactor
  * Record result
