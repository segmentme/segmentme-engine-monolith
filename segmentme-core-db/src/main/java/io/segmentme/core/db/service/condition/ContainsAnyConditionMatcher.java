package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ContainsAnyConditionMatcher extends AbstractContainsConditionMatcher {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.CONTAINS_ANY;

    @Override
    boolean match(Collection<Comparable<Object>> propertyValue, List<Comparable<Object>> castedConditionValues) {
        return propertyValue.stream().anyMatch(it -> castedConditionValues.stream().anyMatch(it::equals));
    }
}
