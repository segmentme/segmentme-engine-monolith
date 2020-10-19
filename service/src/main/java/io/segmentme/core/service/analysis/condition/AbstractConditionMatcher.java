package io.segmentme.core.service.analysis.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;
import io.segmentme.core.service.exception.CriteriaValueLocatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
abstract class AbstractConditionMatcher<T extends AbstractCondition, E, P> implements Matcher<T> {

    @Autowired
    private ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    protected Comparable<Object> castJsonProperty(Object conditionValue, Comparable<Object> value) {
        return (Comparable<Object>) (value.getClass() != conditionValue.getClass() ? mapper.convertValue(conditionValue, value.getClass()) : conditionValue);
    }

    protected P getProperty(String propertyName, ContextValueHolder context) {
        return (P) context.getValue(propertyName);
    }


    public abstract AbstractCondition.ConditionType getType();


    public boolean match(T condition, ContextValueHolder context, WormConsumer worm) {
        P actualValue = null;

        try {
            actualValue = getProperty(condition.getCriteria(), context);
        } catch (CriteriaValueLocatorException ex) {
            log.warn("Unable to locate property {} in context {}, because {}", condition.getCriteria(), context, ex.getMessage());
            Optional.ofNullable(worm).ifPresent(it -> it.accept(condition, ex));
            return !condition.isMatchResult();
        }

        try {
            Boolean aBoolean = checkForNullValid(condition, actualValue);
            if (aBoolean != null) {
                return aBoolean;
            }
            return match(getExpectedValue(condition, actualValue), actualValue);
        }  catch (Exception ex) {
            log.warn("Can't match property {} in context {} , because {}", condition.getCriteria(), context, ex.getMessage());
            Optional.ofNullable(worm).ifPresent(it -> it.accept(condition, ex));
            return !condition.isMatchResult();
        }
    }

    protected abstract E getExpectedValue(T condition, P actualValue);


    abstract Boolean checkForNullValid(T condition, P value);

    abstract boolean match(E expected, P actual);
}
