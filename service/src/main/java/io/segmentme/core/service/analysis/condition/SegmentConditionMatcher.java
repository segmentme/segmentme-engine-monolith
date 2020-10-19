package io.segmentme.core.service.analysis.condition;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.repository.SegmentRepository;
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

    private final SegmentRepository analysisRuleRepository;

    @Lazy
    private final ConditionMatcher conditionMatcher;

    @Lazy
    private final SegmentAnalysisService analysisRuleService;


    @Override
    public boolean match(SegmentCondition condition, ContextValueHolder context, WormConsumer worm) {
        var segment = analysisRuleRepository.findById(condition.getValue())
                .orElseThrow(() -> new RuntimeException(String.format("Segment with id %s doesn't exist", condition.getValue())));

        var conditionMatchResult = analysisRuleService.analyze(context, segment, worm).isValue();

        var result = conditionMatchResult == condition.isMatchResult();

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
