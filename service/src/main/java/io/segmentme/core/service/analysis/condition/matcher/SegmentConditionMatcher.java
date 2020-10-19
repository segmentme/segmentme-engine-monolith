package io.segmentme.core.service.analysis.condition.matcher;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.segment.SegmentAnalysisService;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Data
@Component
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionMatcher extends AbstractConditionMatcher<SegmentCondition, Segment, String> {

    private final AbstractCondition.ConditionType type = AbstractCondition.ConditionType.SEGMENT;

    @Lazy
    private final ConditionMatcher conditionMatcher;

    @Lazy
    private final SegmentAnalysisService analysisRuleService;


    @Override
    public boolean match(SegmentCondition condition, ContextValueHolder context, WormConsumer worm) {
        boolean conditionMatchResult = analysisRuleService.analyze(context, condition.getSegment(), worm).isValue();

        boolean result = conditionMatchResult == condition.isMatchResult();

        Optional.ofNullable(worm).ifPresent(it -> it.accept(condition, conditionMatchResult));

        return result;

    }

    @Override
    protected Segment getExpectedValue(SegmentCondition condition, String actualValue) {
        return null;
    }

    @Override
    Boolean checkForNullValid(SegmentCondition condition, String value) {
        return null;
    }

    @Override
    boolean match(Segment expected, String actual) {
        return false;
    }
}
