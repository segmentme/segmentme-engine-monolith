package io.segmentme.core.db.service.rule.common;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.domain.rule.BooleanAnalysisRule;
import io.segmentme.core.db.service.ContextHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class BooleanAnalysisRuleService extends SimpleAnalysisRuleService<BooleanAnalysisRule> {

    @Getter
    private final AbstractAnalysisRule.RuleType ruleType = AbstractAnalysisRule.RuleType.BOOLEAN;

    @Override
    Boolean getValue(BooleanAnalysisRule rule) {
        return Boolean.TRUE.equals(rule.getValue());
    }

    @Override
    public Boolean getRuleValueIfSatisfy(ContextHolder context, BooleanAnalysisRule rule) {
        return isMatch(rule, context) ? getValue(rule) : false;
    }
}
