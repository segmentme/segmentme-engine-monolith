package io.segmentme.core.service.rule;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule;
import io.segmentme.core.db.service.rule.RuleService;
import io.segmentme.core.service.condition.ConditionManager;
import io.segmentme.core.service.converter.RuleConverter;
import io.segmentme.core.service.dto.rule.AbstractAnalysisRuleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.PRECONDITION;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleManager {

    private final RuleService ruleService;

    private final ConditionManager conditionManager;

    public AbstractAnalysisRuleDto<?> save(AbstractAnalysisRuleDto<?> rule, String contextId, String integrationPointKey) {
        AbstractAnalysisRule<?> analysisRule = RuleConverter.of(rule, contextId, integrationPointKey);
        return RuleConverter.of(ruleService.create(analysisRule));
    }

    public List<AbstractAnalysisRuleDto<?>> save(List<AbstractAnalysisRuleDto<?>> rules, String contextId, String integrationPointKey) {

        List<AbstractAnalysisRule<?>> analysisRules = rules.stream().map(it -> RuleConverter.of(it, contextId, integrationPointKey))
                .collect(Collectors.toList());

        return ruleService.createAll(analysisRules).stream().map(RuleConverter::of).collect(Collectors.toList());
    }

    public List<AbstractAnalysisRuleDto<?>> findByIntegrationPointKey(String integrationPointKey) {
        return ruleService.findByIntegrationPointKey(integrationPointKey).stream()
                .map(RuleConverter::of)
                .collect(Collectors.toList());
    }

    public List<AbstractAnalysisRuleDto<?>> findByContextId(String contextId) {
        return ruleService.findByContextId(contextId).stream().map(RuleConverter::of).collect(Collectors.toList());
    }

    public void delete(String ruleId) {
        ruleService.findById(ruleId).ifPresent(it -> {
            Set<AbstractAnalysisRule<?>> rulesToDelete = new HashSet<>(Collections.singletonList(it));

            if (it.getRuleType() == PRECONDITION) {
                rulesToDelete.addAll(findRelatedConditionToDelete(((PreconditionAnalysisRule) it).getAnalysisRules()));
            }

            List<AbstractCondition<?>> conditions = rulesToDelete.stream().map(AbstractAnalysisRule::getConditions).flatMap(Collection::stream).collect(Collectors.toList());
            conditionManager.deleteEmbeddedConditions(conditions);
            ruleService.deleteAll(rulesToDelete);
        });
    }

    private Collection<AbstractAnalysisRule<?>> findRelatedConditionToDelete(List<? extends AbstractAnalysisRule<?>> rules) {
        Set<AbstractAnalysisRule<?>> relatedRules = rules.stream()
                .filter(it -> it.getRuleType() == PRECONDITION)
                .map(it -> findRelatedConditionToDelete(((PreconditionAnalysisRule) it).getAnalysisRules()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        relatedRules.addAll(rules.stream().filter(AbstractAnalysisRule::isEmbedded).collect(Collectors.toList()));
        return relatedRules;
    }
}
