package io.segmentme.core.service.analysis.condition.matcher;

import io.segmentme.core.db.domain.condition.SimpleCondition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class SimpleConditionMatcher<T extends SimpleCondition<?>> extends AbstractConditionMatcher<T, Comparable<Object>, Comparable<Object>> {

    @Override
    Boolean checkForNullValid(T condition, Comparable<Object> value) {
        if (value != null) {
            return null;
        }
        return condition.isNullValid();
    }


    @Override
    protected Comparable<Object> getExpectedValue(T condition, Comparable<Object> actualValue) {
        return castJsonProperty(condition.getValue(), actualValue);
    }
}
