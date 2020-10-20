package io.segmentme.core.service.analysis.segment.worm;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiConsumer;

@Data
@Slf4j
public class StatisticWorm implements BiConsumer<AbstractCondition, Object> {
    private Map<String, ConditionStatisticEntry> debugResultMap = new HashMap<>();

    @Override
    public void accept(AbstractCondition condition, Object result) {

        var statisticEntry = debugResultMap.computeIfAbsent(condition.getHash(), key -> new ConditionStatisticEntry());

        statisticEntry.numOfInvocation++;
        if (result instanceof Exception) {
            statisticEntry.errors.add(((Exception) result).getMessage());
            return;
        }
        statisticEntry.positiveResult = Objects.equals(condition.isMatchResult(), result);
    }

    @Data
    public static class ConditionStatisticEntry {
        int numOfInvocation;
        boolean positiveResult;

        Set<String> errors = new HashSet<>();
    }
}
