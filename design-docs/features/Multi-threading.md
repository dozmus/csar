Multi-threading
========

# Parsing
__Problem__: Distribute and parse _n_ source files on _k_ threads efficiently.

__Proposed Solution__:  
* Store the complete list of source files to parse in a data structure
* Sort them by file size
* Each thread picks and removes the largest source file from the data structure to parse.

This way we minimize the throughput variance between different threads (e.g. if one thread does much more work than another). This is not perfect though, because file size isn't a comprehensive measure of source file parsing effort.
