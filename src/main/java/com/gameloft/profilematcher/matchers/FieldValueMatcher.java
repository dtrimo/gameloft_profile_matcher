package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;

public interface FieldValueMatcher {

    /**
     * The matcher specifies the type of matching a {@link FieldValueMatcher} does. For example: "min", "max" or "regex".
     *
     * @return the name of the matcher
     */
    String getMatcherName();

    //TODO: Update Javadoc
    /**
     * Determine if a {@code fieldValue} matches a single particular targetValue represented as a {@link JsonNode}.
     * For example, if {@code fieldValue == 2}, {@code target = 5.2} and {@code getMatcherName()} returns {@code "min"},
     * then this method checks exclusively that the minimum condition is respected (that is, that {@code 2 >= 5.2}).
     *
     * @param fieldName the name of the field whose value we check. (used mostly for error reporting)
     * @param fieldValue the value against which the matcher is run
     * @param targetValue a {@link JsonNode} that contains the value to check against
     * @return true iff the fieldValue matches the condition that this matcher implements
     * @throws IllegalArgumentException when the types of the fieldValue and the targetValue are inappropriate for this matcher
     */
    boolean matches(String fieldName, Object fieldValue, JsonNode targetValue) throws IllegalArgumentException;

}
