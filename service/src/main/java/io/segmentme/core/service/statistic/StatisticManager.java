package io.segmentme.core.service.statistic;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.condition.SegmentCondition;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.domain.statistic.AnalyzedData;
import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.db.service.segment.SegmentService;
import io.segmentme.core.db.service.statistic.AnalyzedDataService;
import io.segmentme.core.db.service.statistic.StatisticService;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.core.service.dto.statistic.StatisticLogEntry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
@RequiredArgsConstructor
public class StatisticManager {

    private final StatisticService statisticService;

    private final SegmentService segmentService;

    private final AnalyzedDataService analyzedDataService;

    @Async
    @EventListener
    public void saveStatistic(StatisticLogEntry collectedStatistic) {
        AnalyzedData analyzedData = aggregateAnalyzedData(collectedStatistic);

        StatisticLog statisticLog = new StatisticLog();
        statisticLog.setWorkspaceId(collectedStatistic.getWorkspaceId());
        statisticLog.setAnalysisTime(collectedStatistic.getAnalysisTime());
        statisticLog.setIntegrationPointKey(collectedStatistic.getIntegrationPointKey());
        statisticLog.setAnalyzedDataKey(analyzedData.getHash());
        ContextSchema schema = collectedStatistic.getContextValueHolder().getSchema();
        if (schema != null) {
            statisticLog.setKnownTypes(schema.getInlinePath());
        }
        statisticLog.setSegmentStatistics(getSegmentStatistics(collectedStatistic));
        statisticLog.setConditionStatistics(getConditionsBreakdown(collectedStatistic));

        statisticService.create(statisticLog);
    }

    private AnalyzedData aggregateAnalyzedData(StatisticLogEntry collectedStatistic) {
        AnalyzedData analyzedData = new AnalyzedData();
        analyzedData.setWorkspaceId(collectedStatistic.getWorkspaceId());
        analyzedData.setPayload(collectedStatistic.getRawPayload().toString());
        analyzedData.setNodeValues(prepareNodeValues(collectedStatistic.getContextValueHolder().getValues()));
        analyzedData.setClientId(collectedStatistic.getClientId());
        analyzedData.setIntegrationPointKey(collectedStatistic.getIntegrationPointKey());
        analyzedData.setAnalyzedSegments(collectedStatistic.getAnalyzedSegments().stream().map(Segment::getId).collect(Collectors.toList()));

        analyzedDataService.insertIfNotExists(analyzedData);
        return analyzedData;
    }

    private Map<String, List<Object>> prepareNodeValues(Map<String, Object> values) {
        Map<String, List<Object>> prepared = new HashMap<>();

        values.forEach((key, value) -> {
            List<Object> preparedValue = null;
            if (value == null) {
                return;
            }
            if (List.class.isAssignableFrom(value.getClass())) {
                preparedValue = (List<Object>) value;
            } else {
                preparedValue = new ArrayList<>();
                preparedValue.add(value);
            }
            prepared.put(key, preparedValue);
        });
        return prepared;
    }

    private List<StatisticLog.SegmentStatistic> getSegmentStatistics(StatisticLogEntry collectedStatistic) {
        return collectedStatistic.getAnalyzedSegments().stream()
            .map(it -> {
                SegmentAnalysisResult segmentAnalysisResult = collectedStatistic.getSegmentAnalysisResults()
                    .stream()
                    .filter(result -> result.getHash().equalsIgnoreCase(it.getHash()))
                    .findFirst()
                    .get();
                return new StatisticLog.SegmentStatistic().setSegmentId(it.getId())
                    .setResult(segmentAnalysisResult.isValue())
                    .setAnalysisTime(segmentAnalysisResult.getAnalysisTime())
                    .setConditionsHash(getSegmentConditions(it, new HashMap<>()));
            })
            .collect(Collectors.toList());
    }

    private List<StatisticLog.ConditionStatistic> getConditionsBreakdown(StatisticLogEntry collectedStatistic) {
        return collectedStatistic.getConditionResults().entrySet().stream()
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
                    getSegmentConditions(((SegmentCondition) it).getValue(), conditions);
                }
            })
            .peek(it -> conditions.putIfAbsent(String.valueOf(it.hashCode()), 0))
            .forEach(it -> conditions.computeIfPresent(String.valueOf(it.hashCode()), (s, integer) -> ++integer));

        return conditions;
    }

}
