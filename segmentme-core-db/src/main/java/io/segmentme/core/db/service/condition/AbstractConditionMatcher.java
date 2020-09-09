package io.segmentme.core.db.service.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.AnalysisContext;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
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
    protected Comparable<Object> getProperty(String propertyName, AnalysisContext context) {
        return (Comparable<Object>) PropertyUtils.getProperty(context, propertyName);
    }

    public abstract boolean match(T condition, AnalysisContext context);

    public abstract AbstractCondition.ConditionType getType();
}
