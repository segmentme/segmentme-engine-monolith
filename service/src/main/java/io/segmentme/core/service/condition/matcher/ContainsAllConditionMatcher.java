package io.segmentme.core.service.condition.matcher;

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
public class ContainsAllConditionMatcher extends AbstractContainsConditionMatcher {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.CONTAINS_ALL;

    @Override
    boolean match(Collection<Comparable<Object>> propertyValue, List<Comparable<Object>> castedConditionValues) {
        return propertyValue.containsAll(castedConditionValues);
    }
}
