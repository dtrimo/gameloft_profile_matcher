package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MaxValueValueMatcher implements FieldValueMatcher {

    private static final String DEFAULT_MATCHER_NAME = "max";

    @Getter
    private final String matcherName;

    public MaxValueValueMatcher() {
        this(DEFAULT_MATCHER_NAME);
    }

    public MaxValueValueMatcher(String matcherName) {
        this.matcherName = matcherName;
    }

    @Override
    public boolean matches(String fieldName, Object fieldValue, JsonNode targetValue) {
        Number maxValue = targetValue.numberValue();
        if (maxValue == null) {
            throw new IllegalArgumentException(String.format("%s condition is malformed. Cannot compare %s to %s",
                    matcherName, fieldName, targetValue.toPrettyString()));
        }
        if (!(fieldValue instanceof Number)) {
            throw new IllegalArgumentException(String.format("Cannot check %s condition on field %s, because its value, %s, is not numeric",
                    matcherName, fieldName, fieldValue));
        }
        return ((Number)fieldValue).doubleValue() <= maxValue.doubleValue();
    }

}
