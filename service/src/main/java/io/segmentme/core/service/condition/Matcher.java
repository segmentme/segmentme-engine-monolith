package io.segmentme.core.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.service.ContextHolder;

interface Matcher<T extends AbstractCondition<?>> {

    boolean match(T condition, ContextHolder value);

    AbstractCondition.ConditionType getType();

}
