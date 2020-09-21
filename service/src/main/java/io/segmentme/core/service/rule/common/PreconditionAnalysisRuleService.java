package io.segmentme.core.service.rule.common;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule;
import io.segmentme.core.db.domain.rule.SimpleAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import io.segmentme.core.service.analysis.ContextValueHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
    List<AnalysisResult> analyze(ContextValueHolder context, PreconditionAnalysisRule rule) {
        var isMatched = this.getRuleValueIfSatisfy(context, rule);

        if (!isMatched) {
            return convert(rule, null);
        }

        return Optional.of(rule.getAnalysisRules())
                .stream()
                .map(it -> analyzeRules(context, it))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<AnalysisResult> analyzeRules(ContextValueHolder context, List<? extends AbstractAnalysisRule<?>> analysisRules) {
        return Optional.ofNullable(analysisRules)
                .stream()
                .flatMap(Collection::parallelStream)
                .map(it -> analysisRuleService.analyze(it, context))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    Boolean getValue(PreconditionAnalysisRule rule) {
        return Boolean.TRUE.equals(rule.getValue());
    }

    @Override
    public Boolean getRuleValueIfSatisfy(ContextValueHolder context, PreconditionAnalysisRule rule) {
        return isMatch(rule, context) ? getValue(rule) : false;
    }

    private List<AnalysisResult> convert(final AbstractAnalysisRule<?> rule, Object value) {
        if (rule.getRuleType() == AbstractAnalysisRule.RuleType.PRECONDITION) {
            return Optional.ofNullable(((PreconditionAnalysisRule) rule).getAnalysisRules())
                    .stream()
                    .flatMap(Collection::parallelStream)
                    .map(it -> convert(it, null))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        if (rule.getRuleType() == AbstractAnalysisRule.RuleType.BOOLEAN) {
            value = Boolean.TRUE.equals(value);
        }

        if (rule instanceof SimpleAnalysisRule) {
            return List.of(AnalysisResult.of(rule.getId(), ((SimpleAnalysisRule<?>) rule).getFlags(), value));
        }

        throw new IllegalArgumentException("Unknown rule type " + rule.getRuleType());
    }
}
