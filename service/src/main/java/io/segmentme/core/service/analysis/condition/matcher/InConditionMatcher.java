package io.segmentme.core.service.analysis.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.ArrayCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
class InConditionMatcher extends SimpleConditionMatcher<ArrayCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.IN;

    @Override
    public boolean match(ArrayCondition condition, Comparable<Object> value) {
        return condition.getValue().stream().map(it -> castJsonProperty(it, value)).anyMatch(it -> it.compareTo(value) == 0);
    }
}
