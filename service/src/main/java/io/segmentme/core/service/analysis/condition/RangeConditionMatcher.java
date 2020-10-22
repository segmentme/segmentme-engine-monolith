package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.RangeCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class RangeConditionMatcher extends SimpleConditionMatcher<RangeCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.RANGE;

    @Override
    protected Comparable<Object> getExpectedValue(RangeCondition condition, Comparable<Object> actualValue) {
        RangeCondition.RangeValue rangeValue = condition.getValue();
        Comparable<Object> first = castJsonProperty(rangeValue.getMin(), actualValue);
        Comparable<Object> last = castJsonProperty(rangeValue.getMax(), actualValue);
        return new RangeCondition.RangeValue().setMin(first).setMax(last);
    }

    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        RangeCondition.RangeValue rangeValue = (RangeCondition.RangeValue) expected;
        return rangeValue.compareTo(actual) == 0;
    }
}
