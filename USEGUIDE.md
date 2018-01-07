Use Guide
========

# General
If you want accurate results from `csar.jar` you should ensure that your source code is correct.  
Parsing source code with invalid syntax will lead to an error occurring and it being skipped.
csar currently only supports Java 8, `package-info.java` files introduced in Java 9 will be ignored for partial compatibility.

# Searching
Run csar with an appropriate csar query to find the language element you want. If no error occurs a list of search results will be displayed.

e.g. `SELECT method:def:add(int,int)` will find all method definitions with two integer parameters.

## CLI Return Codes
| Return Code | Description |
|---|---|
| 0 | Successful execution. |
| 1 | Error parsing CLI arguments. |
| 2 | Error parsing csar query. |
| 3 | Error parsing code files. |
| 4 | Error searching code files. |
| 5 | Error reading ignore file. |
