package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.domain.condition.SimpleCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.dto.analysis.DebugResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@Data
@Slf4j
public class DebugWorm implements BiConsumer<AbstractCondition, Object> {
    private Map<Integer, DebugResult> debugResultMap = new HashMap<>();
    private final ContextValueHolder contextValueHolder;
    private final String ARRAY_INDEX_CLEANER = "\\[[0-9]+]";

    @Override
    public void accept(AbstractCondition condition, Object result) {

        //TODO need to change: search by hash

        var debugResult = debugResultMap.computeIfAbsent(condition.hashCode(), key -> new DebugResult());

        if (result instanceof Exception) {
            debugResult.setErrorMessage(((Exception) result).getMessage());
            debugResult.setFinalMatchResult(!condition.isMatchResult());
        } else {
            debugResult.setConditionMatchResult((Boolean) result);
        }

        if (condition instanceof SegmentCondition) {
            SegmentCondition segmentCondition = (SegmentCondition) condition;
            debugResult.setSegmentId(segmentCondition.getValue());
            if (result instanceof Boolean) {
                debugResult.setFinalMatchResult((Boolean) result);
            }
        } else {
            SimpleCondition<?> simpleCondition = (SimpleCondition<?>) condition;
            debugResult.setFinalMatchResult(simpleCondition.isMatchResult() == debugResult.isConditionMatchResult());

            String criteria = simpleCondition.getCriteria();
            String clearPath = criteria.replaceAll(ARRAY_INDEX_CLEANER, StringUtils.EMPTY);

            debugResult.setValue(contextValueHolder.getValue(criteria));
            debugResult.setCriteriaType(contextValueHolder.getSchema().getInlinePath().get(clearPath));
            debugResult.setCriteria(criteria);

        }


    }
}
