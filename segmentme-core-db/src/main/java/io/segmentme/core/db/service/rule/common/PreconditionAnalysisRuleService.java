package io.segmentme.core.db.service.rule.common;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.domain.rule.*;
import io.segmentme.core.db.dto.AnalysisResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
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
    AnalysisResult analyze(AnalysisContextSchema context, PreconditionAnalysisRule rule) {
        throw new IllegalArgumentException("Not supported method");
    }

    @Override
    public List<AnalysisResult> analyze(AnalysisContextSchema context, List<PreconditionAnalysisRule> rules) {

        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }

        return collectValues(context, rules);
    }

    private List<AnalysisResult> collectValues(AnalysisContextSchema context, List<PreconditionAnalysisRule> rules) {

        var processedPreconditions = rules.stream().collect(Collectors.groupingBy(it -> this.getRuleValueIfSatisfy(context, it)));

        var notMatchedRules = processedPreconditions.getOrDefault(false, List.of())
                .stream()
                .map(PreconditionAnalysisRule::getAnalysisRules)
                .flatMap(Collection::stream)
                .map(it -> of(it, null))
                .collect(Collectors.toList());

        var matchedRules = processedPreconditions.getOrDefault(true, List.of())
                .stream()
                .map(PreconditionAnalysisRule::getAnalysisRules)
                .map(it -> analyzeRules(context, it))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        notMatchedRules.addAll(matchedRules);
        return notMatchedRules;
    }

    public List<AnalysisResult> analyzeRules(AnalysisContextSchema context, List<? extends SimpleAnalysisRule<?>> analysisRules) {
        return Optional.ofNullable(analysisRules)
                .stream()
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

    private AnalysisResult of(SimpleAnalysisRule<?> rule, Object value) {
        if (rule.getRuleType() == AbstractAnalysisRule.RuleType.BOOLEAN) {
            value = Boolean.TRUE.equals(value);
        }

        return AnalysisResult.of(rule.getId(), rule.getFlags(), value);
    }
}
