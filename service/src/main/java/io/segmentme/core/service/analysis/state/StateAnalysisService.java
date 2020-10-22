package io.segmentme.core.service.analysis.state;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.state.StateService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import io.segmentme.core.service.analysis.segment.worm.StatisticWorm;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.core.service.dto.statistic.StatisticLogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateAnalysisService {

    private final StateService stateService;

    private final AnalysisService analysisService;

    private final ContextSchemaService contextSchemaService;

    private final ContextValuesExtractor contextValuesExtractor;

    private final WorkspaceService workspaceService;

    private final ApplicationEventPublisher applicationEventPublisher;

    public List<StateAnalysisResult> analyse(String integrationPointKey, JsonNode payload) {
        List<State> sates = stateService.findByIntegrationPointKey(integrationPointKey);
        return sates.stream()
                .filter(it -> analyse(it, payload).isValue())
                .map(it -> StateAnalysisResult.of(it.getName(), it.getValue()))
                .collect(Collectors.toList());
    }

    private SegmentAnalysisResult analyse(State state, JsonNode payload) {
        StatisticLogEntry statisticLogEntry = new StatisticLogEntry();
        StatisticWorm statisticWorm = new StatisticWorm();
        WormConsumer worm = WormConsumer.of(Collections.singletonList(statisticWorm));

        long analyzeStartTime = System.currentTimeMillis();

        Workspace workspace = workspaceService.findByIntegrationPointKey(state.getIntegrationPointKey()).orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        ContextSchema schema = contextSchemaService.findByIntegrationPointKey(state.getIntegrationPointKey()).orElseThrow(() -> new IllegalArgumentException("ContextSchema not found"));

        ContextValueHolder contextValueHolder = contextValuesExtractor.extractValues(payload, schema, workspace.getConfiguration());
        statisticLogEntry.setContextValueHolder(contextValueHolder);

        try {
            SegmentAnalysisResult result = analysisService.analyze(contextValueHolder, state.getSegment(), worm);
            statisticLogEntry.setSegmentAnalysisResults(List.of(result));
            return result;
        } finally {
            statisticLogEntry.setIntegrationPointKey(state.getIntegrationPointKey());
            statisticLogEntry.setAnalyzedSegments(List.of(state.getSegment()));
            statisticLogEntry.setConditionResults(statisticWorm.getStatisticMap());
            statisticLogEntry.setAnalysisTime(System.currentTimeMillis() - analyzeStartTime);
            applicationEventPublisher.publishEvent(statisticLogEntry);
        }
    }
}
