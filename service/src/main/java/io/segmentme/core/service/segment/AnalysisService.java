package io.segmentme.core.service.segment;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.repository.SegmentRepository;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.dto.analysis.*;
import io.segmentme.core.service.segment.common.SegmentAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final SegmentAnalysisService segmentAnalysisService;

    private final SegmentRepository analysisRuleRepository;

    private final ContextSchemaService contextSchemaService;

    private final ContextValuesExtractor contextValuesExtractor;

    private final WorkspaceService workspaceService;


    public AnalysisResult debug(ContextValueHolder context, String segmentId) {
        return debug(context, analysisRuleRepository.findById(segmentId).get());
    }

    public AnalysisResult debug(ContextValueHolder context, Segment segment) {

        Map<String, DebugResult> debugResult = new HashMap<>();

        Function<String, DebugResult> debugWorm = id -> debugResult.computeIfAbsent(id, key -> new DebugResult().setConditionId(key));

        SegmentAnalysisResult result = analyze(context, segment, Optional.of(debugWorm));

        return AnalysisResult.of(Arrays.asList(result), debugResult);
    }

    public List<SegmentAnalysisResult> analyze(ContextValueHolder context, List<Segment> rules) {
        return rules.stream().map(it -> this.analyze(context, it, Optional.empty())).collect(Collectors.toList());
    }

    public List<SegmentAnalysisResult> analyze(String contextId, String integrationPointKey, JsonNode payload) {
        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey).orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        List<ContextSchema> schemas = contextSchemaService.findByIntegrationPointKeys(Collections.singletonList(integrationPointKey), false);

        List<Segment> segments = analysisRuleRepository.findByIntegrationPointKeyAndEmbeddedIsFalse(integrationPointKey);
        if (StringUtils.isNoneBlank(contextId)) {
            schemas = schemas.stream().filter(it -> it.getId().equalsIgnoreCase(contextId)).findFirst().map(Collections::singletonList).orElseThrow(() -> new IllegalArgumentException("Context not found"));
            segments = segments.stream().filter(it -> it.getContextId().equalsIgnoreCase(contextId)).collect(Collectors.toList());
        }

        List<Segment> finalSegments = segments;
        return schemas.stream()
                .map(it -> contextValuesExtractor.extractValues(payload, it, workspace.getConfiguration()))
                .map(it -> this.analyze(it, finalSegments))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    private SegmentAnalysisResult analyze(ContextValueHolder context, Segment rule, Optional<Function<String, DebugResult>> debugWorm) {
        return segmentAnalysisService.analyze(context, rule, debugWorm);
    }
}
