csar (code search and refactor)
========

### Usage
All source code in the working directory will be searched, if the current directory is a git repository then only source
code in the repository (and staging area) will be searched.

```
Usage: java -jar csar.jar [options]
  Options:
  * --query, -q
      Search query
    --threads, -t
      Thread count
      Default: 1
    --verbose, -v
      Verbose output
      Default: false
    --format, -f
      Output format
      Default: PlainText
    --output, -o
      Output file name
    --project-url, --url
      Print project URL
      Default: false
    --help, -h
      Print help information
```

Further Example: `java -jar csar.jar -q myquery`

### Contributing
You can specify space separated arguments in the gradle `run` task as follows:  
`gradle run -Pjargs="-q myquery"`

### Roadmap
1. Implement query parsing
2. Implement searching
3. Implement refactoring
4. Implement language-agnosticism
5. Refactor the project as necessary

Note: Stages 2, 3 and 4 will act as major releases.  
Note: Low priority tasks will be completed intermittently.
