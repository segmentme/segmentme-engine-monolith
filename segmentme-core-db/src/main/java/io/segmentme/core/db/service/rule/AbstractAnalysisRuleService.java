package io.segmentme.core.db.service.rule;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.AnalysisContext;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.service.condition.ConditionMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class AbstractAnalysisRuleService<R> {

    @Autowired
    private ConditionMatcher conditionMatcher;

    abstract AbstractAnalysisRule.RuleType getRuleType();


    public R analyze(AnalysisContext context, AbstractAnalysisRule<?> rule) {

        return null;
    }

    private boolean isMatch(AbstractAnalysisRule<?> rule, AnalysisContext context) {
        if (CollectionUtils.isEmpty(rule.getConditions())) {
            return true;
        }

        return switch (rule.getAggregation()) {
            case OR -> rule.getConditions().stream().anyMatch(condition -> match(condition, context));
            case AND -> rule.getConditions().stream().allMatch(condition -> match(condition, context));
        };
    }


    private boolean match(AbstractCondition<?> condition, AnalysisContext context) {
        return conditionMatcher.match(condition, context);
    }
}
