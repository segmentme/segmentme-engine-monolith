package io.segmentme.core.db.service.rule.common;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class PreconditionAnalysisRuleService extends AbstractAnalysisRuleService<PreconditionAnalysisRule> {

    @Getter
    private final AbstractAnalysisRule.RuleType ruleType = AbstractAnalysisRule.RuleType.PRECONDITION;

    @Lazy
    private final AnalysisRuleService analysisRuleService;

    @Override
    public List<AnalysisResult> analyze(AnalysisContextSchema context, List<PreconditionAnalysisRule> rules) {

        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }

        return collectValues(context, rules);
    }

    private List<AnalysisResult> collectValues(AnalysisContextSchema context, List<PreconditionAnalysisRule> rules) {
        return rules.stream()
                .filter(it -> getRuleValueIfSatisfy(context, it))
                .map(PreconditionAnalysisRule::getAnalysisRules)
                .flatMap(Collection::stream)
                .map(it -> analysisRuleService.analyze(it, context))
                .collect(Collectors.toList());
    }

    @Override
    Boolean getValue(PreconditionAnalysisRule rule) {
        return Boolean.TRUE.equals(rule.getValue());
    }

    @Override
    public Boolean getRuleValueIfSatisfy(AnalysisContextSchema context, PreconditionAnalysisRule rule) {
        return isMatch(rule, context) ? getValue(rule) : false;
    }
}
