package io.segmentme.core.service.rule;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.rule.Segment;
import io.segmentme.core.db.service.rule.RuleService;
import io.segmentme.core.service.condition.ConditionManager;
import io.segmentme.core.service.converter.SegmentConverter;
import io.segmentme.core.service.dto.analysis.rule.SegmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class RuleManager {

    private final RuleService ruleService;

    private final ConditionManager conditionManager;

    public SegmentDto save(SegmentDto rule, String contextId, String integrationPointKey) {
        Segment analysisRule = SegmentConverter.of(rule, contextId, integrationPointKey);
        return SegmentConverter.of(ruleService.create(analysisRule));
    }

    public List<SegmentDto> save(List<SegmentDto> rules, String contextId, String integrationPointKey) {

        List<Segment> analysisRules = rules.stream().map(it -> SegmentConverter.of(it, contextId, integrationPointKey))
                .collect(Collectors.toList());

        return ruleService.createAll(analysisRules).stream().map(SegmentConverter::of).collect(Collectors.toList());
    }

    public List<SegmentDto> findByIntegrationPointKey(String integrationPointKey) {
        return ruleService.findByIntegrationPointKey(integrationPointKey).stream()
                .map(SegmentConverter::of)
                .collect(Collectors.toList());
    }

    public List<SegmentDto> findByContextId(String contextId) {
        return ruleService.findByContextId(contextId).stream().map(SegmentConverter::of).collect(Collectors.toList());
    }

    public void delete(String ruleId) {
        ruleService.findById(ruleId).ifPresent(it -> {
            Set<Segment> rulesToDelete = new HashSet<>(Collections.singletonList(it));

            List<AbstractCondition> conditions = rulesToDelete.stream().map(Segment::getConditions).flatMap(Collection::stream).collect(Collectors.toList());
            conditionManager.deleteEmbeddedConditions(conditions);
            ruleService.delete(it);
        });
    }

    public void unlinkFromIntegrationPoint(String integrationPointKey) {
        List<Segment> byIntegrationPointKey = ruleService.findByIntegrationPointKey(integrationPointKey);
        byIntegrationPointKey.forEach(it -> it.setIntegrationPointKey(null));
        ruleService.update(byIntegrationPointKey);

    }

    public void unlinkFromContext(String contextId) {
        List<Segment> contextRules = ruleService.findByContextId(contextId);
        contextRules.forEach(it -> it.setContextId(null));
        ruleService.update(contextRules);

    }
}
