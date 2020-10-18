package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.domain.condition.SimpleCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.CriteriaValueLocator;
import io.segmentme.core.service.dto.analysis.DebugResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;

@Data
@Slf4j
public class DebugWorm implements BiConsumer<AbstractCondition, Object> {
    private Map<String, DebugResult> debugResultMap = new HashMap<>();
    private final ContextValueHolder contextValueHolder;
    private final String ARRAY_INDEX_CLEANER = "\\[[0-9]+]";

    @Override
    public void accept(AbstractCondition condition, Object result) {
        var identifier = Optional.ofNullable(condition.getId()).orElse(condition.getName());

        var debugResult = debugResultMap.computeIfAbsent(identifier, key -> new DebugResult());
        debugResult.setConditionId(condition.getId()).setConditionName(condition.getName());

        if (result instanceof Exception) {
            debugResult.setErrorMessage(((Exception) result).getMessage());
            debugResult.setFinalMatchResult(!condition.isMatchResult());
        } else {
            debugResult.setConditionMatchResult((Boolean) result);
        }

        if (condition instanceof SegmentCondition) {
            SegmentCondition segmentCondition = (SegmentCondition) condition;
            debugResult.setSegmentId(segmentCondition.getSegment().getId());
            debugResult.setSegmentName(segmentCondition.getSegment().getName());
            if (result instanceof Boolean) {
                debugResult.setFinalMatchResult((Boolean) result);
            }
        } else {
            SimpleCondition<?> simpleCondition = (SimpleCondition<?>) condition;
            debugResult.setFinalMatchResult(simpleCondition.isMatchResult() == debugResult.isConditionMatchResult());

            String criteria = simpleCondition.getCriteria();
            String clearPath = criteria.replaceAll(ARRAY_INDEX_CLEANER, StringUtils.EMPTY);

            debugResult.setValue(CriteriaValueLocator.getCriteriaValue(criteria, contextValueHolder));
            debugResult.setCriteria(criteria);
            debugResult.setCriteriaType(contextValueHolder.getSchema().getInlinePath().get(clearPath));
        }


    }
}
