Ignored Files
========

# Input Files
__Problem__: I want to choose which input files to parse smarter.

Some of the related programs I looked at functioned as follows:

* Search the entire folder recursively
* Ignore version control software files (e.g. `.git` or `.svn` folders)
* Ignore files specified in any `.*ignore` files present.
* Allow users to specify further ignored files in this program-specific ignore file, e.g. `.csarignore`.
* Ignore temporary files, e.g. `foo~`.

__Proposed Solution__:  
It would be optimal if I either: had a command-line flag to search the current folder recursively or do that by default and only use the `-i` flag to not search recursively (i.e. specify specific targets).
