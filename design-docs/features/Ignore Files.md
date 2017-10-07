Ignore Files
========

# Ignore Files (v1)
__Problem__: I want to choose which input files to parse smarter.

Some of the related programs I looked at functioned as follows:

* Search the entire folder recursively
* Ignore version control software files (e.g. `.git` or `.svn` folders)
* Ignore files specified in any `.*ignore` files present.
* Allow users to specify further ignored files in this program-specific ignore file, e.g. `.csarignore`.
* Ignore temporary files, e.g. `foo~`.

__Proposed Solution__:  
It would be optimal if I either: had a command-line flag to search the current folder recursively or do that by default and only use the `-i` flag to not search recursively (i.e. specify specific targets).

__Accepted Solution__:  
I have decided to search the entire working directory by default. I support git ignore files (using the `git ls-files` command) and default to finding any source files (on: error or unavailable).  
Allowing a `.csarignore` file is a stretch goal since it will be more complicated than my current approach and is mostly a vanity feature.

# Ignore Files (v2)
__Solution__:
* Narrow search domain by checked in files for VCS
* Narrow it further by a `.csarignore` file

__Csar Ignore File (.csarignore)__:
* A stand-alone project released under MIT
* Purpose: filter directories using an ignore file
* Should design it so that this is applied within an `Iterator<Path>` which uses `Java8` walk methods to find the files. A `Iterator<File>` could also be used to convert back and forth as necessary by third-parties.
* .gitignore format (to be used)
  * `{blank_line} -> skipped`
  * `#{...} -> comment`
  * `\#{...} -> not comment`
  * `!{...} -> unexcludes a file or path`
  * `\!{...} -> not exclusion`
  * Trailing spaces ignored unless if placed within `\`
  * `{...}/ -> directory, otherwise file`
* TL;DR
  * In general, if not_dir: treat as shell glob pattern (w.r.t. current working directory)
    else: shell glob by `fnmatch(3)` with `FNM_PATHNAME` FLAG
  * Wildcards `* _` should not match directories
  * `/**/` - >= 0 directories matched
  * `{...}/**` - match all inside directory
  * `**/{...}` - match in all directories
  * Otheruses of `**` are invalid
  * And some further details regarding precedence available on the .gitignore website (https://git-scm.com/docs/gitignore)
* Resources
  * https://stackoverflow.com/questions/18722471/when-to-use-double-star-in-glob-syntax-within-java
  * https://github.com/sonatype/jgit-simple/blob/master/src/main/java/org/eclipse/jgit/ignore/IgnoreRules.java
  * http://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
  * http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher%28java.lang.String%29
