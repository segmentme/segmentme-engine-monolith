package io.segmentme.core.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.service.condition.ConditionService;
import io.segmentme.core.service.converter.ConditionConverter;
import io.segmentme.core.service.dto.analysis.conditions.AbstractConditionDto;
import io.segmentme.core.service.rule.SegmentManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.SEGMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionManager {

    private final ConditionService conditionService;

    @Lazy
    private final SegmentManager segmentManager;

    public AbstractConditionDto create(AbstractConditionDto conditions, String contextId) {
        var savedCondition = conditionService.create(ConditionConverter.of(conditions, contextId));
        return ConditionConverter.of(savedCondition);
    }

    public List<AbstractConditionDto> findByContextId(String contextId) {
        var conditions = conditionService.findByContextId(contextId);
        return conditions.stream().map(ConditionConverter::of).collect(Collectors.toList());
    }

    public AbstractConditionDto findByConditionId(String conditionId) {
        return ConditionConverter.of(conditionService.findById(conditionId).orElseThrow(() -> new RuntimeException("Condition doesn't exist")));
    }

    public void unlinkFromContextId(String contextId) {
        var conditions = conditionService.findByContextId(contextId);
        conditions.forEach(it -> it.setContextId(null));
        conditionService.updateAll(conditions);
    }

    public void delete(String conditionId) {
        conditionService.findById(conditionId)
                .ifPresent(it -> {
                    if (it.getType() == SEGMENT) {
                        segmentManager.delete(((SegmentCondition) it).getSegment().getId());
                    }
                    conditionService.delete(it);
                });
    }

    public void deleteEmbeddedConditions(List<AbstractCondition> conditions) {
        conditionService.deleteAll(findRelatedConditionToDelete(conditions));
    }

    private Collection<AbstractCondition> findRelatedConditionToDelete(List<AbstractCondition> conditions) {
        Set<AbstractCondition> relatedConditions = conditions.parallelStream()
                .filter(it -> it.getType() == AbstractCondition.ConditionType.SEGMENT)
                .peek(it -> {
                    if (it.getType() == SEGMENT) {
                        segmentManager.delete(((SegmentCondition) it).getSegment().getId());
                    }
                })
                .collect(Collectors.toSet());

        relatedConditions.addAll(conditions.stream().filter(AbstractCondition::isEmbedded).collect(Collectors.toList()));
        return relatedConditions;
    }
}
