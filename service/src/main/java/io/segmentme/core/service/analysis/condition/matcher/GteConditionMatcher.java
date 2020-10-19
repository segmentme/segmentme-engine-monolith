package io.segmentme.core.service.analysis.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SingleCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class GteConditionMatcher extends SimpleConditionMatcher<SingleCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.GTE;


    @Override
    boolean match(Comparable<Object> expected, Comparable<Object> actual) {
        return actual.compareTo(expected) >= 0;
    }
}
