package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;

public interface FieldMatcher {

    String getMatcherName();

    boolean matches(String fieldName, Object fieldValue, JsonNode conditions) throws IllegalArgumentException;

}
