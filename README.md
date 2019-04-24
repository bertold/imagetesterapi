# Image Tester API

This is an attempt to show how I would like to use the AppliTools image tester
tool as an API. This code wraps invoking the command-line utility behind an
interface.

Note that you must have an account with AppliTools before you can use this tool.
Once the account is setup, you can get the API key to use with the tests.

Example usage:
```java
ImageTester imageTester = new ImageTester(myPropertiesFile)
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

Invoking command-line tools from Java are a bad practice for the following reasons:

1. it is slower than invoking a true API
1. subprocess invoked from java forks entire process - resulting in duplicating the memory needs of the java app 
1. grepping the output of the process for the status is also brittle
1. it takes some effort to pick the version of the utility
