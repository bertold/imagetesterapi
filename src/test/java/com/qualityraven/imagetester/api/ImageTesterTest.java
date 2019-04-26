package com.qualityraven.imagetester.api;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Properties;

import static org.testng.Assert.*;

@Test
public class ImageTesterTest {

    private static final String RESOURCE_PATH = "src/test/resources";
    private static final String TARGET_PATH = "target";
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
            fail("Either set the applitools.apiKey system property or " +
                            "the APPLITOOLS_APIKEY environment variable");
        }
    }

    /**
     * Generates sample PDF files.
     *
     * @throws IOException  in case the PDF creation fails.
     */
    @BeforeClass
    public void createPdfSamples() throws IOException {
        for (int i=1; i<3; i++) {
            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);
                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.setLeading(14);
                    contents.beginText();
                    contents.newLineAtOffset(100, 700);
                    contents.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contents.showText("Acme Corporation");
                    contents.newLine();
                    contents.setFont(PDType1Font.HELVETICA, 12);
                    contents.showText("12345 Test Dr");
                    contents.newLine();
                    contents.showText("Austin, TX 78750");
                    contents.endText();

                    contents.setLeading(18);
                    contents.beginText();
                    contents.newLineAtOffset(400, 700);
                    contents.setFont(PDType1Font.HELVETICA, 24);
                    contents.setNonStrokingColor(Color.DARK_GRAY);
                    contents.showText("INVOICE");

                    contents.newLineAtOffset(80,-20);
                    contents.setFont(PDType1Font.HELVETICA, 18);
                    contents.setNonStrokingColor(Color.LIGHT_GRAY);
                    contents.showText("#" + i);
                    contents.endText();

                    contents.setNonStrokingColor(Color.DARK_GRAY);
                    contents.addRect(100, 630, 400, 20);
                    contents.fill();

                }
                doc.save(new File(TARGET_PATH, "invoice" + i + ".pdf"));
            }
        }
    }


    @Test(dataProvider = "singleFileTestData")
    public void testProperties_singleFile(
            String sourceFile, String targetFile, ResultCode expected) throws IOException {
        Files.copy(
                Paths.get(TARGET_PATH, sourceFile),
                Paths.get(TARGET_PATH, targetFile),
                StandardCopyOption.REPLACE_EXISTING
        );

        Properties props = new Properties();
        props.load(new FileInputStream(new File(RESOURCE_PATH, "happypath.properties")));
        props.setProperty(Parameters.APIKEY.getName(), apiKey);
        ImageTester tester = new ImageTester(props);
        assertEquals(tester.execute(), expected);
    }

    @Test(dataProvider = "singleFileTestData")
    public void testDirectParams_singleFile(String sourceFile, String targetFile, ResultCode expected) throws IOException {
        Files.copy(
                Paths.get(TARGET_PATH, sourceFile),
                Paths.get(TARGET_PATH, targetFile),
                StandardCopyOption.REPLACE_EXISTING
        );

        ImageTester tester = new ImageTester(
                apiKey,
                Collections.singletonMap(Parameters.FOLDER.getName(), new File(TARGET_PATH, targetFile).toString()));
        assertEquals(tester.execute(), expected);
    }
}
