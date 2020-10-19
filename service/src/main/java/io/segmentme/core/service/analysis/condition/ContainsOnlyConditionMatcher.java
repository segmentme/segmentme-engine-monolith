package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Data
@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ContainsOnlyConditionMatcher extends AbstractContainsConditionMatcher {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.CONTAINS_ONLY;


    @Override
    boolean match(Collection<Comparable<Object>> expected, Collection<Comparable<Object>> actual) {
        return actual.containsAll(expected) && actual.size() == expected.size();
    }
}
