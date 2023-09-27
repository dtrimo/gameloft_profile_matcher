package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MinValueValueMatcher implements FieldValueMatcher {

    private static final String DEFAULT_MATCHER_NAME = "min";

    @Getter
    private final String matcherName;

    public MinValueValueMatcher() {
        this(DEFAULT_MATCHER_NAME);
    }

    public MinValueValueMatcher(String matcherName) {
        this.matcherName = matcherName;
    }

    @Override
    public boolean matches(String fieldName, Object fieldValue, JsonNode targetValue) {
        Number minValue = targetValue.numberValue();
        if (minValue == null) {
            throw new IllegalArgumentException(String.format("%s condition is malformed. Cannot compare %s to %s",
                    matcherName, fieldName, targetValue.toPrettyString()));
        }
        if (!(fieldValue instanceof Number)) {
            throw new IllegalArgumentException(String.format("Cannot check %s condition on field %s, because its value, %s, is not numeric",
                    matcherName, fieldName, fieldValue));
        }
        return ((Number)fieldValue).doubleValue() >= minValue.doubleValue();
    }

}
