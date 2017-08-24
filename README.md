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
      Thread count (default: 1)
      Default: 1
    --verbose, -v
      Verbose output (default: false)
      Default: false
    --format, -f
      Output format (default: plain text)
    --output, -o
      Output file name
    --help, -h
      Print help information
```

Further Example: `java -jar csar.jar -q myquery`

### Contributing
You can specify space separated arguments in the gradle `run` task as follows:  
`gradle run -Pjargs="-q myquery"`
