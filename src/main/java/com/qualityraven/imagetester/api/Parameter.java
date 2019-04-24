package com.qualityraven.imagetester.api;

/**
 * Class to capture information about command-line arguments for the
 * Image Tester command-line utility.
 */
public class Parameter {

    private String name;
    private boolean mandatory;

    /**
     * Instantiates a parameter with a name and a mandatory flag.
     *
     * @param name       name of the parameter
     * @param mandatory  true iff the parameter is mandatory
     */
    Parameter(final String name, final boolean mandatory) {
        this.name = name;
        this.mandatory = mandatory;
    }

    /**
     * Instantiate an optional parameter.
     *
     * @param name  name of the parameter
     */
    Parameter(final String name) {
        this(name, false);
    }

    /**
     * Returns the name of the parameter.
     *
     * @return the name of the paramater.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the parameter is mandatory.
     *
     * @return true if the parameter is mandatory.
     */
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public int hashCode() {
        return 31 +
                name.hashCode() +
                (mandatory ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Parameter &&
                ((Parameter) obj).name.equalsIgnoreCase(name) &&
                ((Parameter) obj).mandatory == mandatory;
    }
}
