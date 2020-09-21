package io.segmentme.core.service.rule.common;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.condition.matcher.ConditionMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
abstract class AbstractAnalysisRuleService<A extends AbstractAnalysisRule<?>> {

    @Autowired
    private ConditionMatcher conditionMatcher;

    abstract AbstractAnalysisRule.RuleType getRuleType();

    List<AnalysisResult> analyze(ContextValueHolder context, A rule) {
        return List.of(AnalysisResult.of(rule.getId(), getNames(rule), this.getRuleValueIfSatisfy(context, rule)));
    }

    final boolean isMatch(A rule, ContextValueHolder context) {
        if (CollectionUtils.isEmpty(rule.getConditions())) {
            return true;
        }

        return switch (rule.getAggregation()) {
            case OR -> rule.getConditions().stream().anyMatch(condition -> match(condition, context));
            case AND -> rule.getConditions().stream().allMatch(condition -> match(condition, context));
        };
    }

    Object getValue(A rule) {
        return rule.getValue();
    }

    List<String> getNames(A rule) {
        return Collections.emptyList();
    }

    Object getRuleValueIfSatisfy(ContextValueHolder context, A rule) {
        return isMatch(rule, context) ? rule.getValue() : null;
    }

    private boolean match(AbstractCondition<?> condition, ContextValueHolder context) {
        return conditionMatcher.match(condition, context);
    }
}
