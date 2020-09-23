package io.segmentme.core.service.condition;

import io.segmentme.core.db.service.condition.ConditionService;
import io.segmentme.core.service.converter.ConditionConverter;
import io.segmentme.core.service.dto.component.AbstractConditionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        conditionService.delete(conditionId);
    }
}
