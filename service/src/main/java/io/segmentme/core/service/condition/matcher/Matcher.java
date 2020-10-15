package io.segmentme.core.service.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.dto.analysis.DebugResult;

import java.util.Optional;
import java.util.function.*;

interface Matcher<T extends AbstractCondition> {

    boolean match(T condition, ContextValueHolder value, Optional<Function<String, DebugResult>> debugWorm);

    AbstractCondition.ConditionType getType();

}
