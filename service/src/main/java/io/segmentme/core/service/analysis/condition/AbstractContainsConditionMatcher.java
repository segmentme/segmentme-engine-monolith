package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.ArrayCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractContainsConditionMatcher extends AbstractConditionMatcher<ArrayCondition, Collection<Comparable<Object>>, Collection<Comparable<Object>>> {


    @Override
    protected Collection<Comparable<Object>> getExpectedValue(ArrayCondition condition, Collection<Comparable<Object>> actualValue) {
        Comparable<Object> objectComparable = actualValue.stream().findFirst().orElse(null);

        return condition.getValue()
            .stream()
            .map(conditionValue -> castJsonProperty(conditionValue, objectComparable))
            .collect(Collectors.toList());
    }

    @Override
    Optional<Boolean> checkForNullValid(ArrayCondition condition, Collection<Comparable<Object>> value) {
        return Optional.ofNullable(CollectionUtils.isNotEmpty(value) ? null : condition.isNullValid());

    }
}
