package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SingleCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class LtConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.LT;

    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        return actual.compareTo(expected) < 0;
    }
}
