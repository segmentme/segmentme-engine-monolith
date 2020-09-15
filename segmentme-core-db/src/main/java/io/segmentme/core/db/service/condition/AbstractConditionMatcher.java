package io.segmentme.core.db.service.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.service.ContextHolder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

abstract class AbstractConditionMatcher<T extends AbstractCondition<?>> implements Matcher<T> {

    @Autowired
    private ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    protected Comparable<Object> castJsonProperty(Object conditionValue, Comparable<Object> value) {
        return (Comparable<Object>) (value.getClass() != conditionValue.getClass() ? mapper.convertValue(conditionValue, value.getClass()) : conditionValue);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    protected Comparable<Object> getProperty(String propertyName, ContextHolder context) {
        return (Comparable<Object>) CriteriaValueLocator.getCriteriaValue(propertyName, context);
    }

    public abstract boolean match(T condition, ContextHolder context);

    public abstract AbstractCondition.ConditionType getType();
}
