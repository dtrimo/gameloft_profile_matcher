package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MinValueMatcher implements FieldMatcher {

    private static final String DEFAULT_MATCHER_NAME = "min";

    @Getter
    private final String matcherName;

    public MinValueMatcher() {
        this(DEFAULT_MATCHER_NAME);
    }

    public MinValueMatcher(String matcherName) {
        this.matcherName = matcherName;
    }

    @Override
    public boolean matches(String fieldName, Object fieldValue, JsonNode conditions) {
        if (conditions.has(matcherName)) {
            Number minValue = conditions.get(matcherName).numberValue();
            if (minValue == null) {
                throw new IllegalArgumentException(String.format("%s condition is malformed. Cannot compare %s to %s",
                        matcherName, fieldName, conditions.get(matcherName).toPrettyString()));
            }
            if (!(fieldValue instanceof Number)) {
                throw new IllegalArgumentException(String.format("Cannot check %s condition on field %s, because its value, %s, is not numeric",
                        matcherName, fieldName, fieldValue));
            }
            return ((Number)fieldValue).doubleValue() >= minValue.doubleValue();
        }
        throw new IllegalArgumentException(String.format("Condition %s is not specified by %s", matcherName, conditions.toPrettyString()));
    }

}
