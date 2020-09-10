package io.segmentme.core.db.service.rule.common;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalysisRuleService {

    private final Map<AbstractAnalysisRule.RuleType, AbstractAnalysisRuleService<? extends AbstractAnalysisRule<?>>> analysisRuleService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public AnalysisRuleService(List<? extends AbstractAnalysisRuleService> services) {
        analysisRuleService = services.stream().collect(Collectors.toMap(AbstractAnalysisRuleService::getRuleType, it -> it));
    }

    @SuppressWarnings("unchecked")
    public List<AnalysisResult> analyze(List<? extends AbstractAnalysisRule<?>> rules, AnalysisContextSchema context, AbstractAnalysisRule.RuleType type) {
        return findService(type).analyze(context, (List<AbstractAnalysisRule<?>>) rules);
    }

    public AnalysisResult analyze(AbstractAnalysisRule<?> rule, AnalysisContextSchema context) {
        return findService(rule.getRuleType()).analyze(context, rule);
    }

    @SuppressWarnings("unchecked")
    private AbstractAnalysisRuleService<AbstractAnalysisRule<?>> findService(AbstractAnalysisRule.RuleType type) {
        return ((AbstractAnalysisRuleService<AbstractAnalysisRule<?>>) analysisRuleService.computeIfAbsent(type, key -> {
            throw new IllegalStateException("Unknown condition service type " + key);
        }));
    }
}
