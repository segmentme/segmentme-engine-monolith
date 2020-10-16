package io.segmentme.core.service.analysis.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SingleCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class LtConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.LT;

    @Override
    protected boolean match(SingleCondition condition, Comparable<Object> value) {
        return value.compareTo(castJsonProperty(condition.getValue(), value)) < 0;
    }
}
