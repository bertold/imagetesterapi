# Image Tester API

![Build status](https://travis-ci.org/bertold/imagetesterapi.svg?branch=master)

This is an attempt to show how I would like to use the AppliTools image tester
tool as an API. This code wraps invoking the command-line utility behind an
interface.

Note that you must have an account with AppliTools before you can use this tool.
Once the account is setup, you can get the API key to use with the tests.

Example usage:
```java
ImageTester imageTester = new ImageTester(myPropertiesFile);
ResultCode resultCode = imageTester.execute();

switch (tester.execute()) {
            case SUCCESS:
                // success
                break;
            case FAIL:
                // failure
                break;
            case EXECUTION_ERROR:
                // something else failed
                break;
            default:
                assert false;
}
```

It is also possible to execute tests against multiple files. In this case, ```ImageTester``` returns a failure if any of the files tested fail the visual testing. The individual result of each file can be retrieved using the ```getLastResult()``` method.

For example:
```java
ImageTester imageTester = new ImageTester(myPropertiesFilesWithADirectory);
switch (tester.execute()) {
            case SUCCESS:
                // success
                break;
            case FAIL:
                Map<String,TestResult> results = imageTester.getLastResult();
                for (TestResult result : results.values()) {
                    System.err.printf("File: %s, result: %s, test result URI: %s%n",
                       result.getFilename(), result.getResultCode(), result.getResultURI());l 
                }
                break;
            case EXECUTION_ERROR:
                // something else failed
                break;
            default:
                assert false;  
}
```

Invoking command-line tools from Java are a bad practice for the following reasons:

1. it is slower than invoking a true API
1. subprocess invoked from java forks entire process - resulting in duplicating the memory needs of the java app 
1. grepping the output of the process for the status is also brittle
1. it takes some effort to pick the version of the utility
