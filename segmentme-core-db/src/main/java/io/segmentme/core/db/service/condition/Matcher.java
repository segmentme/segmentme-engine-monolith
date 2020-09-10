package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.AnalysisContextSchema;

interface Matcher<T extends AbstractCondition<?>> {

    boolean match(T condition, AnalysisContextSchema value);

    AbstractCondition.ConditionType getType();

}
