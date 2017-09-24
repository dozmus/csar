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
    --output, -o
      Output file name
    --narrow-search
      Narrow search domain (default: true)
    --project-url, --url
      Print project URL
    --help, -h
      Print help information
```

Example: `java -jar csar.jar SELECT method:def:add -t 4`

### Contributing
You can specify space separated arguments with the gradle `run` task as follows:
`-Pjargs="SELECT method:def:add -t 1 --log-level TRACE"`

### Roadmap
1. Implement query parsing
2. Implement searching
3. Implement refactoring
4. Implement language-agnosticism
5. Refactor the project as necessary

Note: Stages 2, 3 and 4 will act as major releases.  
Note: Low priority tasks will be completed intermittently.
