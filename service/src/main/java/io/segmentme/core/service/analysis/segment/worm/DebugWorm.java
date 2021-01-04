package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.domain.condition.SimpleCondition;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.dto.analysis.DebugResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static io.segmentme.core.service.analysis.CriteriaValueLocator.cleanPath;

@Data
@Slf4j
public class DebugWorm implements BiConsumer<AbstractCondition, Object> {
    private Map<String, DebugResult> debugResultMap = new LinkedHashMap<>();
    private final ContextValueHolder contextValueHolder;

    @Override
    public void accept(AbstractCondition condition, Object result) {

        //TODO need to change: search by hash

        var debugResult = debugResultMap.computeIfAbsent(condition.getHash(), key -> new DebugResult());

        if (result instanceof Exception) {
            debugResult.setErrorMessage(((Exception) result).getMessage());
            debugResult.setFinalMatchResult(!condition.isMatchResult());
        } else {
            debugResult.setConditionMatchResult((Boolean) result);
        }

        if (condition instanceof SegmentCondition) {
            SegmentCondition segmentCondition = (SegmentCondition) condition;
            debugResult.setSegmentId(segmentCondition.getValue().getId());
            if (result instanceof Boolean) {
                debugResult.setFinalMatchResult((Boolean) result);
            }
        } else {
            SimpleCondition<?> simpleCondition = (SimpleCondition<?>) condition;
            debugResult.setFinalMatchResult(simpleCondition.isMatchResult() == debugResult.isConditionMatchResult());

            String criteria = simpleCondition.getCriteria();

            debugResult.setValue(contextValueHolder.getValue(criteria));
            debugResult.setCriteriaType(contextValueHolder.getSchema().getInlinePath().get(cleanPath(criteria)));
            debugResult.setCriteria(criteria);

        }


    }
}
