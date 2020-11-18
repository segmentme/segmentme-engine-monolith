package io.segmentme.core.service.analysis.segment;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.repository.SegmentRepository;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.analysis.segment.worm.DebugWorm;
import io.segmentme.core.service.analysis.segment.worm.StatisticWorm;
import io.segmentme.core.service.analysis.segment.worm.Worm;
import io.segmentme.core.service.analysis.segment.worm.WormConsumer;
import io.segmentme.core.service.dto.analysis.AnalysisResult;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.core.service.dto.statistic.StatisticLogEntry;
import io.segmentme.core.service.exception.ContextSchemaManagerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final SegmentAnalysisService segmentAnalysisService;

    private final SegmentRepository analysisRuleRepository;

    private final ContextSchemaService contextSchemaService;

    private final ContextValuesExtractor contextValuesExtractor;

    private final WorkspaceService workspaceService;

    private final ApplicationEventPublisher applicationEventPublisher;


    public AnalysisResult debug(ContextValueHolder context, String segmentId) {
        return debug(context, analysisRuleRepository.findById(segmentId).get());
    }

    public AnalysisResult debug(ContextValueHolder context, Segment segment) {

        DebugWorm worm = new DebugWorm(context);
        SegmentAnalysisResult result = analyze(context, segment, WormConsumer.of(Collections.singletonList(worm)));

        return AnalysisResult.of(Collections.singletonList(result), worm.getDebugResultMap());
    }

    public AnalysisResult debug(String integrationPointKey, String contextId, JsonNode payload, Segment segment) {
        ContextSchema schema = contextSchemaService.findByIdAndIntegrationPointKey(contextId, integrationPointKey)
            .orElseThrow(() -> new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND));

        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey)
            .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        return debug(contextValuesExtractor.extractValues(payload, schema, workspace.getConfiguration()), segment);
    }

    public List<SegmentAnalysisResult> analyze(ContextValueHolder context, List<Segment> rules, StatisticWorm statisticWorm) {
        WormConsumer worm = WormConsumer.of(Collections.singletonList(statisticWorm));
        return rules.stream().map(it -> this.analyze(context, it, worm)).collect(Collectors.toList());
    }

    public List<SegmentAnalysisResult> analyze(String integrationPointKey, JsonNode payload, Segment segment) {
        return analyze(null, integrationPointKey, payload, List.of(segment));
    }

    public List<SegmentAnalysisResult> analyze(String contextId, String integrationPointKey, JsonNode payload) {
        return analyze(contextId, integrationPointKey, payload, analysisRuleRepository.findByIntegrationPointKey(integrationPointKey));
    }

    private List<SegmentAnalysisResult> analyze(String contextId, String integrationPointKey, JsonNode payload, List<Segment> segments) {
        StatisticLogEntry statisticLogEntry = new StatisticLogEntry();
        StatisticWorm worm = new StatisticWorm();
        long analyzeStartTime = System.currentTimeMillis();

        statisticLogEntry.setIntegrationPointKey(integrationPointKey);
        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey).orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        statisticLogEntry.setWorkspaceId(workspace.getId());
        List<ContextSchema> schemas = contextSchemaService.findByIntegrationPointKeys(Collections.singletonList(integrationPointKey), false);

        if (StringUtils.isNoneBlank(contextId)) {
            schemas = schemas.stream().filter(it -> it.getId().equalsIgnoreCase(contextId)).findFirst().map(Collections::singletonList).orElseThrow(() -> new IllegalArgumentException("Context not found"));
            segments = segments.stream().filter(it -> it.getContextId().equalsIgnoreCase(contextId)).collect(Collectors.toList());
        } else {
            schemas = new ArrayList<>();
            schemas.add(null);
        }

        List<Segment> finalSegments = segments;
        try {
            List<SegmentAnalysisResult> segmentAnalysisResults = schemas.stream()
                .map(it -> contextValuesExtractor.extractValues(payload, it, workspace.getConfiguration()))
                .peek(statisticLogEntry::setContextValueHolder)
                .map(it -> this.analyze(it, finalSegments, worm))
                .flatMap(List::stream).collect(Collectors.toList());
            statisticLogEntry.setSegmentAnalysisResults(segmentAnalysisResults);
            return segmentAnalysisResults;
        } finally {
            statisticLogEntry.setAnalyzedSegments(segments);
            statisticLogEntry.setConditionResults(worm.getStatisticMap());
            statisticLogEntry.setAnalysisTime(System.currentTimeMillis() - analyzeStartTime);
            applicationEventPublisher.publishEvent(statisticLogEntry);
        }
    }

    @SuppressWarnings({"unchecked"})
    private SegmentAnalysisResult analyze(ContextValueHolder context, Segment rule, Worm<?> worm) {
        return segmentAnalysisService.analyze(context, rule, (Worm<Object>) worm);
    }
}
