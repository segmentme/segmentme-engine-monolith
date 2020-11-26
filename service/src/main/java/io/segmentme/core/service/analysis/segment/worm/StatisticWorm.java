package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static io.segmentme.core.service.dto.statistic.StatisticLogEntry.ConditionStatisticEntry;

@Data
@Slf4j
public class StatisticWorm implements BiConsumer<AbstractCondition, Object> {
    private Map<String, ConditionStatisticEntry> statisticMap = new HashMap<>();

    @Override
    public void accept(AbstractCondition condition, Object result) {

        var statisticEntry = statisticMap.computeIfAbsent(condition.getHash(), key -> new ConditionStatisticEntry());

        if (result instanceof Exception) {
            statisticEntry.setErrors(((Exception) result).getMessage());
            return;
        }
        statisticEntry.setCriteria(condition.getCriteria());
        statisticEntry.setResult(Objects.equals(condition.isMatchResult(), result));
    }


}
