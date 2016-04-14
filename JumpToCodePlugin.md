#The JumpToCodePlugin for IntelliJ IDEA

# Introduction #

The JumpToCode plugin waits for requests from external programs to jump to a specific source location.

The idea is that log-file viewers like Chainsaw and Vigilog interact with this plugin.
When viewing a log-entry or stacktrace in Vigilog, users can click on a location and make IDEA jump to the corresponding location in the source code.

Current version of Vigilog (1.3.1) can communicate with IDEA via this  plugin.
see http://vigilog.sourceforge.net/

![http://firewood.googlecode.com/svn/trunk/plugins/idea/JumpToCode/screenshots/vigilog-jumptocode.jpg](http://firewood.googlecode.com/svn/trunk/plugins/idea/JumpToCode/screenshots/vigilog-jumptocode.jpg)

# Details #

This info is only for people that want to integrate with the plugin.
If you just want to use the plugin, installing it should be enough.

---

The plugin features an very simple embedded HTTP server.

By default it listens on port 5986. But that's configurable of course.

It understands only three operations:
  * jump: make IDEA jump to a specific location
  * test: test if IDEA can jump to a specific location (without actually jumping)
  * form: show a form to perform one of the other two operations (for testing)

## Parameters ##

The following parameters are recognized:
| **Long name** | **Short name** | **Purpose** |  **Default** |
|:--------------|:---------------|:------------|:-------------|
|  operation    | o              | which operation to perform (jump|test|form) | jump         |
|  packageName  | p              | name of the package (eg java.lang) |              |
|  fileName     | f              | name of file to jump to (eg String.java) |              |
| lineNumber    | l              | line number to jump to | 0            |
| className     | c              | name of class to jump to |              |

className is only used when packageName and/or fileName is missing.

It's preferable to use packageName + fileName + lineNumber


## Examples: ##

http://localhost:5986/?o=jump&p=java.lang&f=String.java&l=120

http://localhost:5986/?p=java.lang&f=String.java&l=120

=> jump to line 120 of java.lang.String

http://localhost:5986/?o=test&p=com.googlecode.firewood&f=Foo.java&l=120

=> test if file **Foo.java** in package **com.googlecode.firewood** is known to IDEA