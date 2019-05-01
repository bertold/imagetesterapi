package com.qualityraven.imagetester.api;

import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * Wrapper interface for the AppliTools Image Tester command line utility.
 * @see <a href="https://help.applitools.com/hc/en-us/articles/360007188551-Image-Tester-Stand-alone-tool-for-images-comparison">
 *     Image Tester - Stand-alone tool for images comparison</a>
 */
public class ImageTester {

    /**
     * Pattern to match the filename in the image tester output.
     */
    private static final String FILE_PATTERN = "^.* - (.*)$";

    /**
     * Pattern to match the URL in the image tester output in case of a failure.
     */
    private static final String RESULT_PATTERN = "^.*Result url: (.*)$";

    private final Parameters params;

    /**
     * Saves the result of the last test execution in a thread local variable
     * that can be retrieved by the caller.
     */
    private final ThreadLocal<Map<String,TestResult>> lastResult = new ThreadLocal<>();

    /**
     * Instantiates the tester for command-line arguments using the provided properties.
     * @param properties properties
     */
    public ImageTester(Properties properties) {
        params = Parameters.load(properties);
    }

    /**
     * Instantiates the tester using the provided API key and the optional parameters.
     *
     * @param apiKey         Applitools API key
     * @param optionalParams optional parameters
     */
    public ImageTester(String apiKey, Map<String,String> optionalParams) {
        params = Parameters.create(apiKey, optionalParams);
    }

    /**
     * Executes the tester. If there are multiple files tested, then the result code
     * is only set to ResultCode.SUCCESS if *all* of the files passed the visual test.
     * In case of multiple files, the individual results can be retrieved using the
     * {@link #getLastResult()} method.
     *
     * @return the result of the operation
     * @throws IOException in case of an I/O problem
     */
    public ResultCode execute() throws IOException {
        // clear the last result
        lastResult.set(new HashMap<>());

        final Map<String,String> parameters = params.getParamsCopy();
        final List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-jar");
        args.add("ImageTester.jar");
        for (Map.Entry<String,String> param : parameters.entrySet()) {
            args.add("--" + param.getKey());
            // the command line argument may not have a value
            if (!"".equals(param.getValue())) {
                args.add(param.getValue());
            }
        }


        Process process = Runtime.getRuntime().exec(args.toArray(new String[0]));
        final String folder = parameters.get(Parameters.FOLDER.getName());
        if (folder != null && new File(folder).isDirectory()) {
            return parseResultsMultipleFiles(process);
        } else {
            return parseResultsSingleFile(process);
        }
    }

    /**
     * Returns the last results executed by the tester. The keys in this map represent the
     * tested files and the value is the corresponding test result object.
     * Note that the returned map cannot be modified.
     *
     * @return the last results executed by the tester.
     */
    public Map<String,TestResult> getLastResult() {
        if (lastResult.get() == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(lastResult.get());
    }

    private ResultCode parseResultsSingleFile(final Process process) throws IOException {
        final String fileName = params.getParamsCopy().get(Parameters.FOLDER.getName());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("[New]") || line.contains("[Passed]")) {
                    appendLastResult(fileName, ResultCode.SUCCESS);
                    return ResultCode.SUCCESS;
                } else if (line.contains("[Mismatch]")) {
                    appendLastResult(fileName, ResultCode.FAIL);
                    return ResultCode.FAIL;
                }
            }

        }

        appendLastResult(fileName, ResultCode.EXECUTION_ERROR);
        return ResultCode.EXECUTION_ERROR;
    }

    private ResultCode parseResultsMultipleFiles(final Process process) throws IOException {
        ResultCode resultCode = ResultCode.EXECUTION_ERROR;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            String lastFilename = "";
            ResultCode lastResultCode = ResultCode.SUCCESS;

            // Example test output:
            // 	[Mismatch] - invoice-1556315041402.pdf
            //	 + Result url: https://eyes.applitools.com/app/batches/00000251845577515790/00000251845577515634?accountId=x9ruaHXACk6q0GPbGRcyoA~~
            //  or
            //  [Passed] - invoice-1556315041402.pdf
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
                if (line.contains("[New]") || line.contains("[Passed]")) {
                    String fileName = line.replaceFirst(FILE_PATTERN, "$1");
                    if (resultCode != ResultCode.FAIL) {
                        resultCode = ResultCode.SUCCESS;
                    }
                    appendLastResult(fileName, ResultCode.SUCCESS);
                } else if (line.contains("[Mismatch]")) {
                    String fileName = line.replaceFirst(FILE_PATTERN, "$1");
                    resultCode = ResultCode.FAIL;
                    // Save the filename and result code, as the URL of the result will
                    // be in the next line.
                    lastFilename = fileName;
                    lastResultCode = ResultCode.FAIL;
                } else if (line.contains("+ Result url")) {
                    final String resultUrl = line.replaceFirst(RESULT_PATTERN, "$1");
                    appendLastResult(lastFilename, lastResultCode, resultUrl.trim());
                }
            }

        }
        return resultCode;
    }

    private void appendLastResult(String file, ResultCode resultCode) {
        appendLastResult(file, resultCode, "");
    }

    private void appendLastResult(String file, ResultCode resultCode, String uri) {
        final String fileName = file == null ? "" : file;
        lastResult.get().put(fileName, new TestResult(file, resultCode, URI.create(uri)));
    }
}
