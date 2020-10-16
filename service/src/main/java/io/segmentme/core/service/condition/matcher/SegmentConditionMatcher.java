package io.segmentme.core.service.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.service.dto.analysis.DebugResult;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.segment.common.SegmentAnalysisService;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.*;

@Data
@Component
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionMatcher extends AbstractConditionMatcher<SegmentCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.SEGMENT;

    @Lazy
    private final ConditionMatcher conditionMatcher;

    @Lazy
    private final SegmentAnalysisService analysisRuleService;


    @Override
    public boolean match(SegmentCondition condition, ContextValueHolder context, Optional<Function<String, DebugResult>> debugWorm) {
        boolean conditionMatchResult = analysisRuleService.analyze(context, condition.getSegment(), debugWorm).isValue();

        boolean result = conditionMatchResult == condition.isMatchResult();

        debugWorm.map(it -> it.apply(condition.getContextId())).ifPresent(it -> it.setMatchResult(result).setConditionMatchResult(conditionMatchResult));

        return result;

    }
}
