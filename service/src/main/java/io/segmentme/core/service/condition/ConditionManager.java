package io.segmentme.core.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.GroupCondition;
import io.segmentme.core.db.service.condition.ConditionService;
import io.segmentme.core.service.converter.ConditionConverter;
import io.segmentme.core.service.dto.component.AbstractConditionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.GROUP;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionManager {

    private final ConditionService conditionService;

    public AbstractConditionDto<?> create(AbstractConditionDto<?> conditions, String contextId) {
        var savedCondition = conditionService.create(ConditionConverter.of(conditions, contextId));
        return ConditionConverter.of(savedCondition);
    }

    public List<AbstractConditionDto<?>> createAll(List<AbstractConditionDto<?>> conditions, String contextId) {
        var savedCondition = conditionService.createAll(conditions.stream().map(it -> ConditionConverter.of(it, contextId)).collect(Collectors.toList()));
        return savedCondition.stream().map(ConditionConverter::of).collect(Collectors.toList());
    }

    public List<AbstractConditionDto<?>> findByContextId(String contextId) {
        var conditions = conditionService.findByContextId(contextId);
        return conditions.stream().map(ConditionConverter::of).collect(Collectors.toList());
    }

    public void delete(String conditionId) {
        conditionService.findById(conditionId)
                .ifPresent(it -> {
                    if (it.getType() == GROUP) {
                        deleteEmbeddedConditions(((GroupCondition) it).getConditions());
                    }
                    conditionService.delete(it);
                });
    }

    public void deleteEmbeddedConditions(List<AbstractCondition<?>> conditions) {
        conditionService.deleteAll(findRelatedConditionToDelete(conditions));
    }

    private Collection<AbstractCondition<?>> findRelatedConditionToDelete(List<AbstractCondition<?>> conditions) {
        Set<AbstractCondition<?>> relatedConditions = conditions.parallelStream()
                .filter(it -> it.getType() == AbstractCondition.ConditionType.GROUP)
                .map(it -> findRelatedConditionToDelete(((GroupCondition) it).getConditions()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        relatedConditions.addAll(conditions.stream().filter(AbstractCondition::isEmbedded).collect(Collectors.toList()));
        return relatedConditions;
    }
}
