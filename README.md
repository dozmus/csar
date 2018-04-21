csar (code search and refactor)
========

### Usage
All source code in the working directory will be searched, if the current directory is a git repository then only source
code in the repository (and staging area) will be searched. This can be toggled using the `--narrow-search` CLI flag.

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
* Build: `build`
* Run: `:csar-cli:run`
* Distribution jar: `:csar-cli:distJar` (these will be placed in `csar\csar-cli\build\distributions' alongside archive distributions)
* Specify Java arguments to `:csar-cli:run` with `-Pjargs="SELECT method:def:add -t 1 --log-level TRACE"`
