package com.qualityraven.imagetester.api;

import java.net.URI;
import java.util.Objects;

/**
 * Represents the test result. Objects of this class cannot be modified after instantiated.
 */
public class TestResult {
    private final String file;
    private final ResultCode resultCode;
    private final URI resultURI;

    /**
     * Creates a new instance with the provided parameters.
     *
     * @param file        file name
     * @param resultCode  result code
     * @param resultURI   (optional) URI of the test result
     */
    public TestResult(final String file, final ResultCode resultCode, final URI resultURI) {
        this.file = file;
        this.resultCode = resultCode;
        this.resultURI = resultURI;
    }

    /**
     * Creates a new instance with the provided parameters.
     *
     * @param file        file name
     * @param resultCode  result code
     */
    public TestResult(final String file, final ResultCode resultCode) {
        this(file, resultCode, URI.create(""));
    }

    /**
     * Returns the file name of the tested image.
     *
     * @return the file name of the tested image.
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the result code of the test.
     *
     * @return the result code of the test.
     */
    public ResultCode getResultCode() {
        return resultCode;
    }

    /**
     * Returns the URI of the test result. This is only set for failed tests.
     *
     * @return the URI of the test result.
     */
    public URI getResultURI() {
        return resultURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestResult)) return false;
        TestResult that = (TestResult) o;
        return getFile().equals(that.getFile()) &&
                getResultCode() == that.getResultCode() &&
                getResultURI().equals(that.getResultURI());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), getResultCode(), getResultURI());
    }
}
