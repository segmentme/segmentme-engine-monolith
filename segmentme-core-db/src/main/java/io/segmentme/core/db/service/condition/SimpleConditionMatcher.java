package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.SimpleCondition;
import io.segmentme.core.db.service.ContextHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class SimpleConditionMatcher<T extends SimpleCondition<?>> extends AbstractConditionMatcher<T> {

    protected abstract boolean match(T condition, Comparable<Object> value);

    public boolean match(T condition, ContextHolder context) {
        Comparable<Object> propertyValue = null;

        try {
            propertyValue = getProperty(condition.getCriteria(), context);
        } catch (Exception ex) {
            log.info("Unable to resolve property {} in context {} ,because {}", condition.getCriteria(), context, ex.getMessage());
        }

        if (propertyValue == null && condition.isNullValid()) {
            return true;
        } else if (propertyValue == null && !condition.isNullValid()) {
            return false;
        }

        return match(condition, propertyValue);
    }
}
