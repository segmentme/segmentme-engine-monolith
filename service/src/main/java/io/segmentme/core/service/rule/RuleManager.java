package io.segmentme.core.service.rule;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.service.rule.RuleService;
import io.segmentme.core.service.converter.RuleConverter;
import io.segmentme.core.service.dto.rule.AbstractAnalysisRuleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleManager {

    private final RuleService ruleService;

    public List<AbstractAnalysisRuleDto<?>> save(List<AbstractAnalysisRuleDto<?>> rules, String contextId, String integrationPointKey) {

        List<AbstractAnalysisRule<?>> analysisRules = rules.stream().map(it -> RuleConverter.of(it, contextId, integrationPointKey))
                .collect(Collectors.toList());

        return ruleService.createAll(analysisRules).stream().map(RuleConverter::of).collect(Collectors.toList());
    }

    public List<AbstractAnalysisRuleDto<?>> findByIntegrationPointKey(String integrationPointKey){
        return ruleService.findByIntegrationPointKey(integrationPointKey).stream()
                .map(RuleConverter::of)
                .collect(Collectors.toList());
    }

    public void delete(String ruleId) {
        ruleService.delete(ruleId);
    }
}
