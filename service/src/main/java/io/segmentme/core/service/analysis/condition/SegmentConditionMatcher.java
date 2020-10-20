package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.repository.SegmentRepository;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.SegmentAnalysisService;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public boolean match(SegmentCondition condition, ContextValueHolder context, WormConsumer worm) {
        return Optional.ofNullable(worm.findResultInCache(condition))
                .orElseGet(() -> {
                    var conditionMatchResult = analysisRuleService.analyze(context, condition.getValue(), worm).isValue();

                    var result = conditionMatchResult == condition.isMatchResult();

                    worm.accept(condition, conditionMatchResult);

                    worm.addToCache(condition, result);

                    return result;
                });
    }
}
