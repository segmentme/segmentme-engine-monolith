package io.segmentme.core.service.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.RangeCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class RangeConditionMatcher extends SimpleConditionMatcher<RangeCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.RANGE;

    @Override
    protected boolean match(RangeCondition condition, Comparable<Object> value) {
        RangeCondition.RangeValue rangeValue = condition.getValue();
        Comparable<Object> first = castJsonProperty(rangeValue.getMin(), value);
        Comparable<Object> last = castJsonProperty(rangeValue.getMax(), value);
        return value.compareTo(first) >= 0 && value.compareTo(last) <= 0;
    }
}
