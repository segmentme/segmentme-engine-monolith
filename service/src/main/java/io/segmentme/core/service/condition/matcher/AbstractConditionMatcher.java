package io.segmentme.core.service.condition.matcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.CriteriaValueLocator;
import io.segmentme.core.service.dto.analysis.DebugResult;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;
import java.util.function.*;

@Slf4j
abstract class AbstractConditionMatcher<T extends AbstractCondition> implements Matcher<T> {

    @Autowired
    private ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    protected Comparable<Object> castJsonProperty(Object conditionValue, Comparable<Object> value) {
        return (Comparable<Object>) (value.getClass() != conditionValue.getClass() ? mapper.convertValue(conditionValue, value.getClass()) : conditionValue);
    }

    @SneakyThrows
    protected Comparable<Object> getProperty(String propertyName, ContextValueHolder context) {
        return castIfRequired(CriteriaValueLocator.getCriteriaValue(propertyName, context), propertyName);
    }

    public abstract boolean match(T condition, ContextValueHolder context, Optional<Function<String, DebugResult>> debugWorm);


    public abstract AbstractCondition.ConditionType getType();

    @SuppressWarnings("unchecked")
    private Comparable<Object> castIfRequired(Object propertyValue, String propertyName) {
        if (!(propertyValue instanceof Collection<?>)) {
            return (Comparable<Object>) propertyValue;
        }

        var collectionProperty = (Collection<Comparable<Object>>) propertyValue;

        if (CollectionUtils.size(collectionProperty) > 1) {
            log.warn("Property {} is a collections with size {}", propertyName, CollectionUtils.size(collectionProperty));
            throw new RuntimeException("Property is a collection with a size greater than one");
        }

        return collectionProperty.stream().findFirst().orElseThrow(() -> new RuntimeException("Collection property is empty"));
    }

}
