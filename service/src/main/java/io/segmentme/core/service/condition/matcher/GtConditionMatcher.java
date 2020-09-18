package io.segmentme.core.service.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SingleCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class GtConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.GT;

    @Override
    protected boolean match(SingleCondition condition, Comparable<Object> value) {
        return value.compareTo(castJsonProperty(condition.getValue(), value)) > 0;
    }
}
