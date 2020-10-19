package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;

interface Matcher<T extends AbstractCondition> {

    boolean match(T condition, ContextValueHolder value, WormConsumer worm);

    AbstractCondition.ConditionType getType();

}
