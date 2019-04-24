package com.qualityraven.imagetester.api;

/**
 * Match levels supported by the tool.
 */
public enum MatchLevel {
    /**
     * Matches only if the document has not changed at all.
     */
    STRICT("Strict"),

    /**
     * Matches if only colors are different.
     */
    CONTENT("Content"),

    /**
     * Matches if the layout has not changed.
     */
    LAYOUT("Layout"),

    /**
     * TBD
     */
    LAYOUT2("Layout2");


    private final String name;


    /**
     * Instantiates a MatchLevel enum with a specified name
     * @param name name of the match level
     */
    MatchLevel(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
