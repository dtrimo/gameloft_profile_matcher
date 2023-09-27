package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MatcherService {

    private static final String COLLECTION_FIELD_MARKER = "|";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private List<FieldMatcher> fieldMatchers;

    private Map<String, FieldMatcher> fieldMatcherMap;

    @Transactional
    public boolean matches(Object object, String matchers) {
        try {
            return matches(object, mapper.readTree(matchers));
        } catch (JsonProcessingException e) {
            log.error("Could not process matchers {}", matchers, e);
            return false;
        }
    }

    //All the keys of the matchers object impose restrictions. We apply a logical AND to all these restrictions.
    //If the matchers node is an array, we apply a logical OR to these restrictions
    private boolean matches(Object object, JsonNode matchers) {
        if (matchers.isArray()) {
            Iterator<JsonNode> childMatchers = matchers.elements();
            while (childMatchers.hasNext()) {
                JsonNode childMatcher = childMatchers.next();
                if (matches(object, childMatcher)) {
                    return true;
                }
            }
            return false;
        }
        Iterator<Map.Entry<String, JsonNode>> individualMatchers = matchers.fields();
        while (individualMatchers.hasNext()) {
            Map.Entry<String, JsonNode> entry = individualMatchers.next();
            if (!evaluateFieldMatcher(entry.getKey(), object, entry.getValue())) {
                return false;
            }
        }
        return true;
    }


    private boolean evaluateFieldMatcher(String fieldName, Object targetObject, JsonNode conditions) {
        boolean isCollectionField = false;
        String collectionOperation = null;
        String originalFieldName = fieldName;
        if (fieldName.contains(COLLECTION_FIELD_MARKER)) {
            String[] parts = fieldName.split("\\" + COLLECTION_FIELD_MARKER);
            if (parts.length != 2) {
                log.warn("Invalid collection field {}", fieldName);
                return false;
            }
            isCollectionField = true;
            fieldName = parts[0];
            collectionOperation = parts[1];
        }

        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(targetObject.getClass(), fieldName);
        if (propertyDescriptor == null || propertyDescriptor.getReadMethod() == null) {
            log.warn("Could not access property {} of class {}", fieldName, targetObject.getClass());
            return false;
        }
        try {
            Object fieldValue = propertyDescriptor.getReadMethod().invoke(targetObject);
            if (isCollectionField) {
                if (!(fieldValue instanceof Collection)) {
                    log.warn("Field {} is not a collection", fieldName);
                    return false;
                }
                Stream<?> elements = ((Collection)fieldValue).stream();
                switch (collectionOperation) {
                    case "none_match" : return elements.noneMatch(element -> matches(element, conditions));
                    case "any_match" : return elements.anyMatch(element -> matches(element, conditions));
                    case "all_match" : return elements.allMatch(element -> matches(element, conditions));
                    default: throw new IllegalArgumentException(String.format("The supported collection predicates are 'none_match'," +
                            " 'any_match' and 'all_match'. Please change the field '%s' to one of '%s|none_match', '%s|any_match' or" +
                            " '%s|all_match' ", originalFieldName, fieldName, fieldName, fieldName));
                }
            }
            return checkPrimitiveFieldMatch(fieldName, fieldValue, conditions);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.warn("Could not access property {} of class {}", fieldName, targetObject.getClass(), e);
            return false;
        }
    }

    private boolean checkPrimitiveFieldMatch(String fieldName, Object fieldValue, JsonNode conditions) {
        switch (conditions.getNodeType()) {
            case BOOLEAN: return Objects.equals(fieldValue, conditions.booleanValue());
            case NUMBER: return Objects.equals(fieldValue, conditions.numberValue());
            case STRING: return Objects.equals(fieldValue, conditions.textValue());
            case ARRAY: {
                Iterator<JsonNode> elements = conditions.elements();
                while (elements.hasNext()) {
                    JsonNode element = elements.next();
                    if (fieldValue instanceof Boolean && element.isBoolean() && Objects.equals(fieldValue, element.booleanValue()) ||
                            fieldValue instanceof Number && element.isNumber() && Objects.equals(fieldValue, element.numberValue()) ||
                            fieldValue instanceof String && element.isTextual() && Objects.equals(fieldValue, element.textValue())) {
                        return true;
                    }
                }
                return false;
            }
            case OBJECT: {
                Iterator<String> specifiedConditions = conditions.fieldNames();
                while (specifiedConditions.hasNext()) {
                    String fieldMatcherName =  specifiedConditions.next();
                    FieldMatcher fieldMatcher = fieldMatcherMap.get(fieldMatcherName);
                    if (fieldMatcher == null) {
                        log.warn("Unknown field matcher {}", fieldMatcherName);
                        return false;
                    }
                    try {
                        if (!fieldMatcher.matches(fieldName, fieldValue, conditions)) {
                            return false;
                        }
                    } catch (IllegalArgumentException e) {
                        log.warn("Could not evaluate matcher {}", fieldMatcherName, e);
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    public void computeFieldMatchersMap() {
        if (fieldMatchers == null || fieldMatchers.isEmpty()) {
            fieldMatcherMap = new HashMap<>();
            return;
        }
        fieldMatcherMap = fieldMatchers.stream().collect(Collectors.toMap(FieldMatcher::getMatcherName, Function.identity()));
    }

}
