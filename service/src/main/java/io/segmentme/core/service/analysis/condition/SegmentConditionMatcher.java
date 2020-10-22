package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.repository.SegmentRepository;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.SegmentAnalysisService;
import io.segmentme.core.service.analysis.segment.worm.Worm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Data
@Component
@RequiredArgsConstructor
public class SegmentConditionMatcher implements Matcher<SegmentCondition> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.SEGMENT;

    private final SegmentRepository analysisRuleRepository;

    @Lazy
    private final ConditionMatcher conditionMatcher;

    @Lazy
    private final SegmentAnalysisService analysisRuleService;


    @Override
    public boolean match(SegmentCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return worm.computeResult(condition.getHash(), matchFunction(condition, context, worm)) == condition.isMatchResult();
    }

    private Function<String, Boolean> matchFunction(SegmentCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return hash -> {
            var conditionMatchResult = analysisRuleService.analyze(context, condition.getValue(), worm).isValue();
            worm.apply(condition, conditionMatchResult);
            return conditionMatchResult;
        };
    }
}
