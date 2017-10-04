Indexing Project Code
========

# Indexing Project Code
__Problem__: Improve project parsing speed by indexing previously parsed, unmodified code.

__Proposed Solution__:  
* Store a `(Path, LastModified, Code)` relation somewhere, preferably in a `.csar` directory.
* The `LastModified` date is the last time that file was modified, as of when it was parsed.
  We can use this to determine which files need updating. This can be error-prone, since
  last modified dates can be spoofed, but it would offer no advantage.
* Approach 1: Flat Files  
  Either map files to a parallel hierarchy in here, or, store them in files where their names
  are the hashes of the input files. The second approach is not any slower than the first.
* Approach 2: Database (SQLite)

This is a stretch goal, since it is a performance improvement.