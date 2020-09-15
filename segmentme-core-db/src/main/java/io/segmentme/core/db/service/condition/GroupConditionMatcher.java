package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.GroupCondition;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.service.ContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupConditionMatcher extends AbstractConditionMatcher<GroupCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.GROUP;

    @Lazy
    private final ConditionMatcher conditionMatcher;

    @Override
    public boolean match(GroupCondition conditions, ContextHolder context) {
        return conditions.getAggregation() == AbstractAnalysisRule.AggregationType.OR
                ? conditions.getConditions().stream().anyMatch(condition -> conditionMatcher.match(condition, context))
                : conditions.getConditions().stream().allMatch(condition -> conditionMatcher.match(condition, context));
    }
}
