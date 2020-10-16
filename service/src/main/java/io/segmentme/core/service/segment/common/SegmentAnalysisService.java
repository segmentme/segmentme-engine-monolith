package io.segmentme.core.service.segment.common;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.condition.matcher.ConditionMatcher;
import io.segmentme.core.service.dto.analysis.DebugResult;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentAnalysisService {

    private final ConditionMatcher conditionMatcher;

    public SegmentAnalysisResult analyze(ContextValueHolder context, Segment segment, Optional<Function<String, DebugResult>> debugWorm) {
        return SegmentAnalysisResult.of(segment.getName(), segment.getId(), this.getSegmentValueIfSatisfy(context, segment, debugWorm));
    }

    final boolean isMatch(Segment segment, ContextValueHolder context, Optional<Function<String, DebugResult>> debugWorm) {
        if (CollectionUtils.isEmpty(segment.getConditions())) {
            return true;
        }

        return switch (segment.getAggregation()) {
            case OR -> segment.getConditions().stream().anyMatch(condition -> match(condition, context, debugWorm));
            case AND -> segment.getConditions().stream().allMatch(condition -> match(condition, context, debugWorm));
        };
    }

    private boolean getSegmentValueIfSatisfy(ContextValueHolder context, Segment segment, Optional<Function<String, DebugResult>> debugWorm) {
        return isMatch(segment, context, debugWorm) == segment.isMatchResult();
    }

    private boolean match(AbstractCondition condition, ContextValueHolder context, Optional<Function<String, DebugResult>> debugWorm) {
        return conditionMatcher.match(condition, context, debugWorm);
    }
}
