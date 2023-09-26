package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameloft.profilematcher.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MatcherService {

    private ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public boolean matches(Profile profile, String matchers) {
        try {
            return parseMatchers(matchers).test(profile);
        } catch (JsonProcessingException e) {
            log.error("Could not process matchers {}", matchers, e);
            return false;
        }
    }

    private Predicate<Profile> parseMatchers(String matchers) throws JsonProcessingException {
        JsonNode matcherNode = mapper.reader().readTree(matchers);
        List<Predicate<Profile>> individualMatchers = new ArrayList<>();
        matcherNode.fields().forEachRemaining(entry ->individualMatchers.add(parseMatcher(entry)));
        return profile -> individualMatchers.stream().anyMatch(matcher -> matcher.test(profile));
    }

    private <T> Predicate<T> parseMatcher(Map.Entry<String, JsonNode> matcher) {
        return t -> {
            switch (matcher.getKey()) {
                case "has":
                    return false;
                case "does_not_have":
                    return false;
                default:
                    return evaluateFieldMatcher(matcher.getKey(), t, matcher.getValue());
            }
        };
    }

    private <T> boolean evaluateFieldMatcher(String fieldName, T targetObject, JsonNode conditions) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(targetObject.getClass(), fieldName);
        if (propertyDescriptor == null || propertyDescriptor.getReadMethod() == null) {
            log.warn("Could not access property {} of class {}", fieldName, targetObject.getClass());
            return false;
        }
        try {
            Object fieldValue = propertyDescriptor.getReadMethod().invoke(targetObject);
            return checkMatch(fieldName, fieldValue, conditions);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.warn("Could not access property {} of class {}", fieldName, targetObject.getClass(), e);
            return false;
        }
    }

    private boolean checkMatch(String fieldName, Object fieldValue, JsonNode conditions) {
        switch (conditions.getNodeType()) {
            case BOOLEAN: return Objects.equals(fieldValue, conditions.booleanValue());
            case NUMBER: return Objects.equals(fieldValue, conditions.numberValue());
            case STRING: return Objects.equals(fieldValue, conditions.textValue());
            case ARRAY: {
                Iterator<JsonNode> elements = conditions.elements();
                while (elements.hasNext()) {
                    JsonNode element = elements.next();
                    if (fieldValue instanceof Boolean && element.isBoolean() && Objects.equals(fieldValue, conditions.booleanValue()) ||
                            fieldValue instanceof Number && element.isNumber() && Objects.equals(fieldValue, conditions.numberValue()) ||
                            fieldValue instanceof String && element.isTextual() && Objects.equals(fieldValue, conditions.textValue())) {
                        return true;
                    }
                }
                return false;
            }
            case OBJECT: {
                if (conditions.has("min")) {
                    Number minValue = conditions.get("min").numberValue();
                    if (minValue == null) {
                        log.warn("min condition is malformed. Cannot compare to {}", conditions.get("min").asText());
                        return false;
                    }
                    if (!(fieldValue instanceof Number)) {
                        log.warn("Cannot check min condition on field {}, because it is not numeric", fieldName);
                        return false;
                    }
                    if (((Number)fieldValue).doubleValue() < minValue.doubleValue()) {
                        return false;
                    }
                }

                if (conditions.has("max")) {
                    Number maxValue = conditions.get("max").numberValue();
                    if (maxValue == null) {
                        log.warn("max condition is malformed. Cannot compare to {}", conditions.get("max").asText());
                        return false;
                    }
                    if (!(fieldValue instanceof Number)) {
                        log.warn("Cannot check max condition on field {}, because it is not numeric", fieldName);
                        return false;
                    }
                    if (((Number)fieldValue).doubleValue() > maxValue.doubleValue()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}
