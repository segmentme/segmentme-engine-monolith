package io.segmentme.core.service.rule.common;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.rule.Segment;
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

    public SegmentAnalysisResult analyze(ContextValueHolder context, Segment rule, Optional<Function<String, DebugResult>> debugWorm) {
        return SegmentAnalysisResult.of(rule.getName(), rule.getId(), this.getRuleValueIfSatisfy(context, rule, debugWorm));
    }

    final boolean isMatch(Segment rule, ContextValueHolder context, Optional<Function<String, DebugResult>> debugWorm) {
        if (CollectionUtils.isEmpty(rule.getConditions())) {
            return true;
        }

        return switch (rule.getAggregation()) {
            case OR -> rule.getConditions().stream().anyMatch(condition -> match(condition, context, debugWorm));
            case AND -> rule.getConditions().stream().allMatch(condition -> match(condition, context, debugWorm));
        };
    }

    private boolean getRuleValueIfSatisfy(ContextValueHolder context, Segment rule, Optional<Function<String, DebugResult>> debugWorm) {
        return isMatch(rule, context, debugWorm) == rule.isMatchResult();
    }

    private boolean match(AbstractCondition condition, ContextValueHolder context, Optional<Function<String, DebugResult>> debugWorm) {
        return conditionMatcher.match(condition, context, debugWorm);
    }
}
