package io.segmentme.core.service.rule.common;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import io.segmentme.core.service.analysis.ContextValueHolder;
import lombok.extern.slf4j.Slf4j;
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

    public List<AnalysisResult> analyze(AbstractAnalysisRule<?> rule, ContextValueHolder context) {
        return findService(rule.getRuleType()).analyze(context, rule);
    }

    @SuppressWarnings("unchecked")
    private AbstractAnalysisRuleService<AbstractAnalysisRule<?>> findService(AbstractAnalysisRule.RuleType type) {
        return ((AbstractAnalysisRuleService<AbstractAnalysisRule<?>>) analysisRuleService.computeIfAbsent(type, key -> {
            throw new IllegalStateException("Unknown condition service type " + key);
        }));
    }
}
