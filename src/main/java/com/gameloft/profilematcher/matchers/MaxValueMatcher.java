package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MaxValueMatcher implements FieldMatcher {

    private static final String DEFAULT_MATCHER_NAME = "max";

    @Getter
    private final String matcherName;

    public MaxValueMatcher() {
        this(DEFAULT_MATCHER_NAME);
    }

    public MaxValueMatcher(String matcherName) {
        this.matcherName = matcherName;
    }

    @Override
    public boolean matches(String fieldName, Object fieldValue, JsonNode conditions) {
        if (conditions.has(matcherName)) {
            Number maxValue = conditions.get(matcherName).numberValue();
            if (maxValue == null) {
                throw new IllegalArgumentException(String.format("%s condition is malformed. Cannot compare %s to %s",
                        matcherName, fieldName, conditions.get(matcherName).toPrettyString()));
            }
            if (!(fieldValue instanceof Number)) {
                throw new IllegalArgumentException(String.format("Cannot check %s condition on field %s, because its value, %s, is not numeric",
                        matcherName, fieldName, fieldValue));
            }
            return ((Number)fieldValue).doubleValue() <= maxValue.doubleValue();
        }
        throw new IllegalArgumentException(String.format("Condition %s is not specified by %s", matcherName, conditions.toPrettyString()));
    }

}
