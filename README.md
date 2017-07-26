csar (code search and refactor)
========

### Usage
```
Usage: java -jar csar.jar [options] [Input files]
  Options:
  * --query, -q
      Search query
    --threads, -t
      Thread count
      Default: 1
    --verbose, -v
      Verbose output
      Default: false
    --help, -h
      Help information
```

Further Example: `java -jar csar.jar -q myquery src/*.java`

### Contributing
You can specify space separated arguments in the gradle `run` task as follows:  
`gradle run -Pjargs="-q hello1 -i hello2"`