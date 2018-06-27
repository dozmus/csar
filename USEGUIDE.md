Use Guide
========

# General
If you want accurate results from csar you should ensure that your source code is correct.
csar currently only supports Java 8, `package-info.java` and `module-info.java` files will be ignored for partial
Java 9 compatibility.
Parsing source code with invalid syntax will lead to an error occurring and it being skipped.

# Narrowing Domain in VCS Repositories
csar will detect and narrow its domain in supported VCS repositories (git, svn, and hg), this can be disabled using
 `--narrow-search`.  
This requires their pure CLI frontends to be installed otherwise an error will be printed.  
This is the following if a suitable git frontend is not installed: `Error running git ls-files: git is not recognized`.

# Plugins
Plugins were introduced into csar to provide language-agnosticism.
This means that the API itself does not define the specifics of how to perform a search or a refactor, this is instead provided by plugins.

Plugins are creating as follows:
* Create a new project targeting the JVM which has `csar-api' as a compile-time dependency.
* Create a class which implements `CsarPlugin` from `csar-api'.
  This must have a public 0-arguments constructor, and also must implement all of the behaviour defined by the interface.
  Furthermore, you should provide the error listeners with the appropriate error details if one occurs.
* Define a resource for the JAR, which should reside in the `META-INF/services` folder within it.
  The resource file's name should be `org.qmul.csar.plugin.CsarPlugin` and its content the fully qualified name of your plugin.
* Compile the source code into a JAR.

The following is the plugin skeleton. 
```java
package my.package;

/**
  * A csar plugin.
  */
public class MyCsarPlugin implements CsarPlugin {

  @Override
  public void parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount) {
    // Parse code...
  }

  @Override
  public void postprocess(int threadCount) {
    // Post-process code...
  }

  @Override
  public List<Result> search(CsarQuery csarQuery, int threadCount) {
    // Search code...
  }

  @override
  public List<Result> refactor(CsarQuery csarQuery, List<Result> searchResults, int threadCount) {
    // Refactor code...
  }

  @Override
  public void addErrorListener(CsarErrorListener errorListener) {
    // Add the error listener...
  }

  @Override
  public void removeErrorListener(CsarErrorListener errorListener) {
    // Remove the error listener...
  }
}
```

This plugin would have the fully qualified name `my.package.MyCsarPlugin'.

# Csar Query Language
Each csar query is comprised of four parts, but only the first is required:
* searchTarget - The element to select.
* containsQuery - What searchTarget should contain within it. (unsupported)
* fromTarget - Where searchTarget should be found (specify comma separated file names without extensions).
* refactorDescriptor - The transformation to apply to searchTarget. (unsupported)

Which are combined in the following way: `SELECT {searchTarget} FROM {fromTarget}`.

# Searching
Run csar with an appropriate csar query to find the language element(s) you want.
If no error occurs a list of search results will be displayed.

e.g. `SELECT method:def:add(int,int)` will find all method definitions with two integer parameters.

# Running csar from within another process
You can run csar from another process as you would with any other piece of software.
Csar's results output to the standard output stream, and its logging outputs to the standard error output stream.

## CLI Return Codes
| Return Code | Description |
|---|---|
| 0 | Successful execution. |
| 1 | Error parsing CLI arguments. |
| 2 | Error parsing csar query. |
| 3 | Error parsing code files. |
| 4 | Error searching code files. |
| 5 | Error initializing csar. |
| 6 | Error post-processing code files. |
| 7 | Error formatting search results. |
| 8 | Error formatting refactor results. |
| 9 | Error refactoring code files. |
