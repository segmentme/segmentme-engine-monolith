package io.segmentme.core.service.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.service.condition.ConditionService;
import io.segmentme.core.service.converter.ConditionConverter;
import io.segmentme.core.service.dto.component.AbstractConditionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionManager {

    private final ConditionService conditionService;

    public AbstractConditionDto<?> create(AbstractConditionDto<?> conditionDto) {
        AbstractCondition<?> savedCondition = conditionService.create(ConditionConverter.of(conditionDto));
        return ConditionConverter.of(savedCondition);
    }
}
