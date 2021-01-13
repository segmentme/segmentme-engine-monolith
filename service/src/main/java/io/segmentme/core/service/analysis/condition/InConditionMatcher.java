package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.ArrayCondition;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Service
class InConditionMatcher extends AbstractConditionMatcher<ArrayCondition, List<Object>, Comparable<Object>> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.IN;


    @Override
    protected List<Object> getExpectedValue(ArrayCondition condition, Comparable<Object> actualValue) {
        return condition.getValue();
    }

    @Override
    Optional<Boolean> checkForNullValid(ArrayCondition condition, Comparable<Object> value) {
        return Optional.ofNullable(value != null ? null : condition.isNullValid());
    }

    @Override
    boolean match(List<Object> expected, Comparable<Object> actual) {
        return expected.stream().map(it -> castJsonProperty(it, actual)).anyMatch(it -> it.compareTo(actual) == 0);
    }
}
