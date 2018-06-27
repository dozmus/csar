csar (code search and refactor)
========

csar is a query-driven, semantics-based, and language-agnostic code searching and refactoring tool.
Currently it only supports searching for methods and method calls in Java projects, and refactoring them by
changing their names or parameters.

You can find more information in the project report and the [use guide](USEGUIDE.md).

### Example queries
* `SELECT method:use:final String appender(int, String)
   This will find all method calls which have the signature `final String appender(int, String)`.

* `SELECT method:def:public int add(4) REFACTOR rename:sum`
   This will find all methods called `add` with four arguments of any type, and rename them to `sum'.

* `SELECT method:def:public int double(2) REFACTOR changeparam:int a`
   This will find and replace the parameter's of this method to be just `int a`.

### Usage
All source code in the working directory will be searched, if the current directory is a git repository then only source code in the repository (and staging area) will be searched.
This can be toggled using the `--narrow-search` CLI flag.

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
    --narrow-search
      Narrow search domain (default: true)
    --ignore-file
      Ignore file (default: .csarignore)
    --project-url, --url
      Print project URL
    --help, -h
      Print help information
```

Example (more in the project report): `java -jar csar.jar SELECT method:def:add -t 4`.

### Gradle cheat sheet
* You can invoke gradle using the `gradlew` scripts, there is one for Windows and one for Unix.
* Build: `build`
* Run: `:csar-cli:run`
* Build distribution JARs: `:csar-cli:distJar` (these will be placed in `csar\csar-cli\build\distributions' alongside archive distributions)
* Specify Java arguments to `:csar-cli:run` with `-Pjargs="SELECT method:def:add -t 1 --log-level TRACE"`
