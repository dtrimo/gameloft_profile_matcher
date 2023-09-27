package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class RegexValueValueMatcher implements FieldValueMatcher {

    private static final String DEFAULT_MATCHER_NAME = "regex";

    @Getter
    private final String matcherName;

    public RegexValueValueMatcher() {
        this(DEFAULT_MATCHER_NAME);
    }

    public RegexValueValueMatcher(String matcherName) {
        this.matcherName = matcherName;
    }

    @Override
    public boolean matches(String fieldName, Object fieldValue, JsonNode targetValue) {
        if (!targetValue.isTextual()) {
            throw new IllegalArgumentException(String.format("%s condition is malformed. %s is not a string, hence it cannot be a regex",
                    matcherName, targetValue.toPrettyString()));
        }
        if (!(fieldValue instanceof String)) {
            throw new IllegalArgumentException(String.format("Cannot check %s condition on field %s, because its value, %s, is not a string",
                    matcherName, fieldName, fieldValue));
        }
        String regexString = targetValue.textValue();
        try {
            Pattern pattern = Pattern.compile(regexString);
            return pattern.matcher((String)fieldValue).matches();
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException(String.format("%s condition is malformed. %s is not a valid regex",
                    matcherName, regexString), e);
        }
    }

}
