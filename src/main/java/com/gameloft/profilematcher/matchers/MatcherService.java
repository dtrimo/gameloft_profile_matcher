package com.gameloft.profilematcher.matchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private List<FieldValueMatcher> fieldValueMatchers;

    private Map<String, FieldValueMatcher> fieldMatcherMap;

    public boolean matches(Object object, String matchers) {
        try {
            LinkedList<String> fieldNames = new LinkedList<>();
            fieldNames.add(".");
            return matches(object, mapper.readTree(matchers), fieldNames);
        } catch (JsonProcessingException e) {
            log.error("Could not process matchers {}", matchers, e);
            return false;
        }
    }

    private boolean matches(Object object, JsonNode matchers, LinkedList<String> fieldNames) {
        switch (matchers.getNodeType()) {
            case ARRAY: {
                Iterator<JsonNode> childMatchers = matchers.elements();
                while (childMatchers.hasNext()) {
                    JsonNode childMatcher = childMatchers.next();
                    if (matches(object, childMatcher, fieldNames)) {
                        return true;
                    }
                }
                return false;
            }
            case OBJECT: {
                Iterator<Map.Entry<String, JsonNode>> fields = matchers.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    if (isMatcher(entry.getKey())) {
                        //if the key is a matcher, treat the value as its argument
                        try {
                            if (!fieldMatcherMap.get(entry.getKey()).matches(fieldNames.getLast(), object, entry.getValue())) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("Error encountered evaluating matchers", e);
                            return false;
                        }
                    } else {
                        // otherwise, the key must be a field name. we extract the field value and do the matching recursively.
                        // We, however, want to treat fields whose values is a collection in a special manner. Such fields
                        // must be specified by using a suffix to indicate how to treat collection elements: "none_match",
                        // "any_match" or "all_match"
                        String fieldName = entry.getKey();
                        LinkedList<String> extendedFieldNames = new LinkedList<>(fieldNames);
                        JsonNode fieldConditions = entry.getValue();

                        if (!isCollectionField(fieldName)) {
                            extendedFieldNames.add(fieldName);
                            if (!matches(extractFieldValue(fieldName, object), fieldConditions, extendedFieldNames)) {
                                return false;
                            }
                        } else {
                            String[] parts = fieldName.split("\\|");
                            if (parts.length != 2) {
                                log.warn("Invalid collection field {}", fieldName);
                                return false;
                            }
                            fieldName = parts[0];
                            extendedFieldNames.add(fieldName);
                            Object fieldValue = extractFieldValue(fieldName, object);
                            if (!(fieldValue instanceof Collection)) {
                                log.warn("Field {} is not a collection", String.join(",", extendedFieldNames));
                                return false;
                            }
                            if (!collectionMatches(parts[1], ((Collection)fieldValue).stream(), fieldConditions, extendedFieldNames)) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            case BOOLEAN:
                return Objects.equals(object, matchers.booleanValue());
            case NUMBER:
                return Objects.equals(((Number)object).doubleValue(), matchers.numberValue().doubleValue());
            case STRING:
                return Objects.equals(object, matchers.textValue());
            default: return false;
        }
    }

    private boolean collectionMatches(String collectionOperation, Stream<?> collectionFieldElements,
                                      JsonNode fieldConditions, LinkedList<String> fieldNames) {
        String fieldName = fieldNames.getLast();
        String originalFieldName = fieldName + "|" + collectionOperation;
        switch (collectionOperation) {
            case "none_match" : return collectionFieldElements.noneMatch(element -> matches(element, fieldConditions, fieldNames));
            case "any_match" : return collectionFieldElements.anyMatch(element -> matches(element, fieldConditions, fieldNames));
            case "all_match" : return collectionFieldElements.allMatch(element -> matches(element, fieldConditions, fieldNames));
            default: throw new IllegalArgumentException(String.format("The supported collection predicates are 'none_match'," +
                    " 'any_match' and 'all_match'. Please change the field '%s' to one of '%s|none_match', '%s|any_match' or" +
                    " '%s|all_match' ", originalFieldName, fieldName, fieldName, fieldName));
        }
    }

    private Object extractFieldValue(String fieldName, Object targetObject) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(targetObject.getClass(), fieldName);
        if (propertyDescriptor == null || propertyDescriptor.getReadMethod() == null) {
            throw new IllegalArgumentException(
                    String.format("Could not access property %s of class %s", fieldName, targetObject.getClass()));
        }
        try {
            return propertyDescriptor.getReadMethod().invoke(targetObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException(
                    String.format("Could not access property %s of class %s", fieldName, targetObject.getClass()), e);
        }
    }

    private boolean isMatcher(String fieldName) {
        return fieldMatcherMap.containsKey(fieldName);
    }

    private boolean isCollectionField(String fieldName) {
        return fieldName.endsWith("|none_match") || fieldName.endsWith("|any_match") || fieldName.endsWith("|all_match");
    }

    @PostConstruct
    public void computeFieldMatchersMap() {
        if (fieldValueMatchers == null || fieldValueMatchers.isEmpty()) {
            fieldMatcherMap = new HashMap<>();
            return;
        }
        fieldMatcherMap = fieldValueMatchers.stream().collect(Collectors.toMap(FieldValueMatcher::getMatcherName, Function.identity()));
    }

}
