package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.ContextSchema;

interface Matcher<T extends AbstractCondition<?>> {

    boolean match(T condition, ContextSchema value);

    AbstractCondition.ConditionType getType();

}
