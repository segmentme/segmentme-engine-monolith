package io.segmentme.core.service.analysis.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.worm.Worm;
import io.segmentme.core.service.exception.CriteriaValueLocatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

@Slf4j
abstract class AbstractConditionMatcher<T extends AbstractCondition, E, P> implements Matcher<T> {

    @Autowired
    private ObjectMapper mapper;

    private Class<P> comparableValueClass;

    protected AbstractConditionMatcher() {
        comparableValueClass = (Class) ((ParameterizedType) this.getAbstractConditionParameterizedType(this.getClass()).getActualTypeArguments()[2]).getRawType();
    }

    private ParameterizedType getAbstractConditionParameterizedType(Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {

            ParameterizedType genericSuperclass1 = (ParameterizedType) genericSuperclass;
            Class<?> rawType = (Class<?>) ((ParameterizedType) genericSuperclass).getRawType();
            if (rawType.getGenericSuperclass() != Object.class) {
                return getAbstractConditionParameterizedType(rawType);
            }
            return genericSuperclass1;
        } else {
            return getAbstractConditionParameterizedType((Class<?>) genericSuperclass);
        }

    }

    @SuppressWarnings("unchecked")
    protected Comparable<Object> castJsonProperty(Object conditionValue, Comparable<Object> value) {
        return (Comparable<Object>) (value.getClass() != conditionValue.getClass() ? mapper.convertValue(conditionValue, value.getClass()) : conditionValue);
    }


    protected P getProperty(String propertyName, ContextValueHolder context) {
        return context.getValue(propertyName, comparableValueClass);
    }




    public boolean match(T condition, ContextValueHolder context, Worm<Object> worm) {
        P actualValue = null;

        try {
            actualValue = getProperty(condition.getCriteria(), context);
        } catch (CriteriaValueLocatorException ex) {
            log.warn("Unable to locate property {} in context {}, because {}", condition.getCriteria(), context, ex.getMessage());
            Optional.ofNullable(worm).ifPresent(it -> it.apply(condition, ex));
            return !condition.isMatchResult();
        }

        try {
            Boolean aBoolean = checkForNullValid(condition, actualValue);
            if (aBoolean != null) {
                return aBoolean;
            }
            return match(getExpectedValue(condition, actualValue), actualValue);
        } catch (Exception ex) {
            log.warn("Can't match property {} in context {} , because {}", condition.getCriteria(), context, ex.getMessage());
            Optional.ofNullable(worm).ifPresent(it -> it.apply(condition, ex));
            return !condition.isMatchResult();
        }
    }

    protected abstract E getExpectedValue(T condition, P actualValue);


    abstract Boolean checkForNullValid(T condition, P value);

    abstract boolean match(E expected, P actual);
}
