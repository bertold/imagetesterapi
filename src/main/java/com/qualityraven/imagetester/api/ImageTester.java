package com.qualityraven.imagetester.api;

import java.io.*;
import java.util.*;

/**
 * Wrapper interface for the AppliTools Image Tester command line utility.
 * @see <a href="https://help.applitools.com/hc/en-us/articles/360007188551-Image-Tester-Stand-alone-tool-for-images-comparison">
 *     Image Tester - Stand-alone tool for images comparison</a>
 */
public class ImageTester {

    private final Parameters params;

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
     * Executes the tester.
     *
     * @return the result of the operation
     * @throws IOException in case of an I/O problem
     */
    public ResultCode execute() throws IOException {
        final List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-jar");
        args.add("ImageTester.jar");
        for (Map.Entry<String,String> param : params.getParamsCopy().entrySet()) {
            args.add("--" + param.getKey());
            // the command line argument may not have a value
            if (!"".equals(param.getValue())) {
                args.add(param.getValue());
            }
        }
        Process process = Runtime.getRuntime().exec(args.toArray(new String[0]));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        ResultCode resultCode = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains("[New]") || line.contains("[Passed]")) {
                resultCode = ResultCode.SUCCESS;
                break;
            }
            if (line.contains("[Mismatch]")) {
                resultCode = ResultCode.FAIL;
                break;
            }
        }

        reader.close();

        if (resultCode == null) {
            resultCode = ResultCode.EXECUTION_ERROR;
        }

        return resultCode;
    }


}
