package com.qualityraven.imagetester.api;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Properties;

@Test
public class ImageTesterTest {

    private static final String RESOURCE_PATH = "src/test/resources";
    private String apiKey;

    @DataProvider
    public Object[][] singleFileTestData() {
        return new Object[][]{
                { "invoice1.pdf", "invoice.pdf", ResultCode.SUCCESS},
                { "invoice1.pdf", "invoice.pdf", ResultCode.SUCCESS},
                { "invoice2.pdf", "invoice.pdf", ResultCode.FAIL}

        };
    }

    @BeforeClass
    public void setAPIKey() {
        // Try the system property first
        apiKey = System.getProperty("applitools.apiKey");
        // If missing, read from the environment
        if (null == apiKey || apiKey.isEmpty()) {
            apiKey = System.getenv("APPLITOOLS_APIKEY");
        }
        if (null == apiKey || apiKey.isEmpty()) {
            Assert.fail("Either set the applitools.apiKey system property or " +
                            "the APPLITOOLS_APIKEY environment variable");
        }
    }


    @Test(dataProvider = "singleFileTestData")
    public void testProperties_singleFile(
            String sourceFile, String targetFile, ResultCode expected) throws IOException {
        Files.copy(
                Paths.get(RESOURCE_PATH, sourceFile),
                Paths.get(RESOURCE_PATH, targetFile),
                StandardCopyOption.REPLACE_EXISTING
        );

        Properties props = new Properties();
        props.load(new FileInputStream(new File(RESOURCE_PATH, "happypath.properties")));
        props.setProperty(Parameters.APIKEY.getName(), apiKey);
        ImageTester tester = new ImageTester(props);
        Assert.assertEquals(expected, tester.execute());
    }

    @Test(dataProvider = "singleFileTestData")
    public void testDirectParams_singleFile(String sourceFile, String targetFile, ResultCode expected) throws IOException {
        Files.copy(
                Paths.get(RESOURCE_PATH, sourceFile),
                Paths.get(RESOURCE_PATH, targetFile),
                StandardCopyOption.REPLACE_EXISTING
        );

        ImageTester tester = new ImageTester(
                apiKey,
                Collections.singletonMap(Parameters.FOLDER.getName(), new File(RESOURCE_PATH, targetFile).toString()));
        Assert.assertEquals(expected, tester.execute());
    }
}
