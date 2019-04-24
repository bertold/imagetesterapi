package com.qualityraven.imagetester.api;

import java.util.*;

/**
 * Parameters supported by the Image Tester utility.
 */
public class Parameters {

    // Parameters supported by the Image Tester utility
    public static final Parameter APPNAME = new Parameter("AppName");
    public static final Parameter HOSTAPP = new Parameter("hostApp");
    public static final Parameter AUTOSAVE = new Parameter("autoSave");
    public static final Parameter BASELINE = new Parameter("baseline");
    public static final Parameter BRANCH = new Parameter("branch");
    public static final Parameter DPI = new Parameter("dpi");
    public static final Parameter FOLDER = new Parameter("folder");
    public static final Parameter APIKEY = new Parameter("apiKey", true);
    public static final Parameter LOGFILE = new Parameter("logFile");
    public static final Parameter MATCHLEVEL = new Parameter("matchLevel");
    public static final Parameter HOSTOS = new Parameter("hostOs");
    public static final Parameter PROXY = new Parameter("proxy");
    public static final Parameter PARENTBRANCH = new Parameter("parentBranch");
    public static final Parameter PRESERVETESTNAMES = new Parameter("preserveTestNames");
    public static final Parameter PDFPASSWORD = new Parameter("PDFPassword");
    public static final Parameter SERVER = new Parameter("server");
    public static final Parameter SELECTEDPAGES = new Parameter("selectedPages");
    public static final Parameter VIEWPORTSIZE = new Parameter("viewportsize");

    private static final Map<String,Parameter> REGISTRY = new HashMap<>();
    static {
        REGISTRY.put(APPNAME.getName(), APPNAME);
        REGISTRY.put(HOSTAPP.getName(), HOSTAPP);
        REGISTRY.put(AUTOSAVE.getName(), AUTOSAVE);
        REGISTRY.put(BASELINE.getName(), BASELINE);
        REGISTRY.put(BRANCH.getName(), BRANCH);
        REGISTRY.put(DPI.getName(), DPI);
        REGISTRY.put(FOLDER.getName(), FOLDER);
        REGISTRY.put(APIKEY.getName(), APIKEY);
        REGISTRY.put(LOGFILE.getName(), LOGFILE);
        REGISTRY.put(MATCHLEVEL.getName(), MATCHLEVEL);
        REGISTRY.put(HOSTOS.getName(), HOSTOS);
        REGISTRY.put(PROXY.getName(), PROXY);
        REGISTRY.put(PARENTBRANCH.getName(), PARENTBRANCH);
        REGISTRY.put(PRESERVETESTNAMES.getName(), PRESERVETESTNAMES);
        REGISTRY.put(PDFPASSWORD.getName(), PDFPASSWORD);
        REGISTRY.put(SERVER.getName(), SERVER);
        REGISTRY.put(SELECTEDPAGES.getName(), SELECTEDPAGES);
        REGISTRY.put(VIEWPORTSIZE.getName(), VIEWPORTSIZE);
    }

    private Map<String,String> params;

    private Parameters() {
        params = new HashMap<>();
    }

    /**
     * Returns the command-line arguments as parameters loaded from the provided properties file.
     *
     * @param properties properties file with command line arguments
     * @return the command-line arguments as parameters loaded from the provided properties file.
     */
    static Parameters load(final Properties properties) {
        final Parameters params = new Parameters();
        for (Parameter param : REGISTRY.values()) {
            final String value = properties.getProperty(param.getName());
            if (value == null) {
                if (param.isMandatory()) {
                    throw new IllegalArgumentException("Missing required parameter " + param.getName());
                }
            } else {
                if ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value)) {
                    params.setValue(param.getName(), "");
                } else {
                    params.setValue(param.getName(), value);
                }
            }
        }
        return params;
    }

    /**
     * Returns parameters using the provided API key and other optional command-line arguments in the form of
     * argument name and value pairs.
     *
     * @param apiKey           API key
     * @param optionalParams   optional parameters provided as parameter name and value pairs
     * @return parameters using the provided API key and other optional command-line arguments in the form of
     *         argument name and value pairs.
     */
    static Parameters create(final String apiKey, Map<String,String> optionalParams) {
        final Parameters params = new Parameters();
        params.setValue(APIKEY.getName(), apiKey);

        for (Parameter param : REGISTRY.values()) {
            if (param.equals(APIKEY)) {
                continue;
            }
            final String value = optionalParams.get(param.getName());
            if (value == null) {
                if (param.isMandatory()) {
                    throw new IllegalArgumentException("Missing required parameter " + param.getName());
                }
            } else {
                params.setValue(param.getName(), value);
            }
        }

        return params;
    }

    /**
     * Returns the copy of the parameters.
     *
     * @return the copy of the parameters.
     */
    public Map<String,String> getParamsCopy() {
        return new HashMap<>(params);
    }

    private void setValue(String name, String value) {
        params.put(name, value);
    }

}
