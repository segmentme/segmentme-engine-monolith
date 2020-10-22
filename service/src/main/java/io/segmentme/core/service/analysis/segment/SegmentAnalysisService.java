package io.segmentme.core.service.analysis.segment;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.condition.ConditionMatcher;
import io.segmentme.core.service.analysis.segment.worm.Worm;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentAnalysisService {

    private final ConditionMatcher conditionMatcher;

    public SegmentAnalysisResult analyze(ContextValueHolder context, Segment segment, Worm<Object> worm) {
        return SegmentAnalysisResult.of(segment.getName(), segment.getId(), this.getSegmentValueIfSatisfy(context, segment, worm));
    }

    final boolean isMatch(Segment segment, ContextValueHolder context, Worm<Object> worm) {
        if (CollectionUtils.isEmpty(segment.getConditions())) {
            return true;
        }

        return switch (segment.getAggregation()) {
            case OR -> segment.getConditions().stream().anyMatch(condition -> match(condition, context, worm));
            case AND -> segment.getConditions().stream().allMatch(condition -> match(condition, context, worm));
        };
    }

    private boolean getSegmentValueIfSatisfy(ContextValueHolder context, Segment segment, Worm<Object> worm) {
        return isMatch(segment, context, worm) == segment.isMatchResult();
    }

    private boolean match(AbstractCondition condition, ContextValueHolder context, Worm<Object> worm) {
        return conditionMatcher.match(condition, context, worm);
    }
}
