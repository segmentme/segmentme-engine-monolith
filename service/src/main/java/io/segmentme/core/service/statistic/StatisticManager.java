package io.segmentme.core.service.statistic;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.db.service.rule.SegmentService;
import io.segmentme.core.db.service.statistic.StatisticService;
import io.segmentme.core.service.dto.statistic.StatisticLogEntry;
import lombok.Data;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class StatisticManager {

    private final StatisticService statisticService;

    private SegmentService segmentService;


    @EventListener
    public void saveStatistic(StatisticLogEntry statisticLogEntry) {
        StatisticLog statisticLog = new StatisticLog();
        statisticLog.setAnalysisTime(statisticLogEntry.getAnalysisTime());
        statisticLog.setIntegrationPointKey(statisticLog.getIntegrationPointKey());
        ContextSchema schema = statisticLogEntry.getContextValueHolder().getSchema();
        if (schema != null) {
            statisticLog.setKnownTypes(schema.getInlinePath());
        }
        statisticLog.setSegmentStatistics(getSegmentStatistics(statisticLogEntry));
        statisticLog.setConditionStatistics(getConditionsBreakdown(statisticLogEntry));
        statisticLog.setNodeValues(statisticLogEntry.getContextValueHolder().getValues());

        statisticService.create(statisticLog);
    }

    private List<StatisticLog.SegmentStatistic> getSegmentStatistics(StatisticLogEntry statisticLogEntry) {
        return statisticLogEntry.getAnalyzedSegments().stream()
            .map(it -> new StatisticLog.SegmentStatistic().setId(it.getId()).setResult(statisticLogEntry.getSegmentAnalysisResults().stream().filter(result -> result.getSegmentId().equalsIgnoreCase(it.getId())).findFirst().get().isValue()).setConditionsHash(getSegmentConditions(it, new HashMap<>()))).collect(Collectors.toList());
    }

    private List<StatisticLog.ConditionStatistic> getConditionsBreakdown(StatisticLogEntry statisticLogEntry) {
        return statisticLogEntry.getConditionResults().entrySet().stream()
            .map(it -> new StatisticLog.ConditionStatistic()
                .setHash(String.valueOf(it.getKey()))
                .setCriteria(it.getValue().getCriteria())
                .setErrors(it.getValue().getErrors()))
            .collect(Collectors.toList());
    }


    private Map<String, Integer> getSegmentConditions(Segment segment, Map<String, Integer> conditions) {

        segment.getConditions().stream()
            .peek(it -> {
                if (it.getType() == AbstractCondition.ConditionType.SEGMENT) {
                    getSegmentConditions(segmentService.findById(it.getCriteria()).get(), conditions);
                }
            })
            .peek(it -> conditions.putIfAbsent(String.valueOf(it.hashCode()), 0))
            .forEach(it -> conditions.computeIfPresent(String.valueOf(it.hashCode()), (s, integer) -> ++integer));

        return conditions;
    }

}
