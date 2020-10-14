package io.segmentme.core.service.rule;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.dto.AnalysisResult;
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.rule.common.AnalysisRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRuleService analysisRuleService;

    private final AbstractAnalysisRuleRepository analysisRuleRepository;

    private final ContextSchemaService contextSchemaService;

    private final ContextValuesExtractor contextValuesExtractor;

    private final WorkspaceService workspaceService;

    public List<AnalysisResult> analyze(ContextValueHolder context) {
        //TODO need to find rules in db by params... user_id or other key
        return analyze(context, analysisRuleRepository.findByPreconditionIdIsNull());
    }

    public List<AnalysisResult> analyze(ContextValueHolder context, List<AbstractAnalysisRule<?>> rules) {
        return rules.stream().map(it -> analysisRuleService.analyze(it, context))
            .flatMap(Collection::parallelStream)
            .collect(Collectors.toList());
    }

    public List<AnalysisResult> analyze(String contextId, String integrationPointKey, JsonNode payload) {
        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey).orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        List<ContextSchema> schemas = contextSchemaService.findByIntegrationPointKeys(Collections.singletonList(integrationPointKey), false);
        if (StringUtils.isNoneBlank(contextId)) {
            schemas = schemas.stream().filter(it -> it.getId().equalsIgnoreCase(contextId)).findFirst().map(Collections::singletonList).orElseThrow(() -> new IllegalArgumentException("Context not found"));
        }

        return schemas.stream()
            .map(it -> contextValuesExtractor.extractValues(payload, it, workspace.getConfiguration()))
            .map(this::analyze).flatMap(List::stream).collect(Collectors.toList());
    }
}
