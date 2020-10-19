package io.segmentme.core.service.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.analysis.CriteriaValueLocator;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.converter.ContextSchemaConverter;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import io.segmentme.core.service.exception.ContextSchemaManagerException;
import io.segmentme.core.service.exception.ContextSchemaValidationException;
import io.segmentme.core.service.exception.error.ContextMangerErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaManager {

    private final ContextSchemaValidationService validationService;

    private final ContextSchemaService contextSchemaService;

    private final ContextSchemaResolver contextSchemaResolver;

    private final WorkspaceService workspaceService;

    private final SegmentManager segmentManager;

    private final ContextValuesExtractor contextValuesExtractor;

    private final ObjectMapper objectMapper;

    public ContextSchemaHolder create(String integrationPointKey, SchemaNode root, String name, String rawPayload) {
        if (workspaceService.findByIntegrationPointKey(integrationPointKey).isEmpty()) {
            throw new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND);
        }
        ContextSchema contextSchema = contextSchemaResolver.resolve(root);
        contextSchema.setIntegrationPointKey(integrationPointKey);
        contextSchema.setName(name);
        contextSchema.setRawPayload(rawPayload);
        validateContextSchemaAndThrowAnError(contextSchema);

        if (StringUtils.isNoneBlank(rawPayload)) {
            try {
                JsonNode rawContext = objectMapper.readValue(rawPayload, JsonNode.class);
                contextSchema.setNodeValues(getNodeValues(contextSchema, contextValuesExtractor.extractValues(rawContext, null, null)));
            } catch (JsonProcessingException e) {
                log.error("Unable to parse json", e);
            }
        }

        return ContextSchemaConverter.toHolder(contextSchemaService.create(contextSchema));
    }

    public ContextSchemaHolder updateContextSchema(String id, ContextSchemaHolder holder) {
        return contextSchemaService.findById(id).map(it -> {
            ContextSchema contextSchema = contextSchemaResolver.resolve(holder.getRootNode());
            it.setInlinePath(contextSchema.getInlinePath());
            it.setRootNode(contextSchema.getRootNode());
            it.setIntegrationPointKey(holder.getIntegrationPointKey());
            it.setName(holder.getName());
            return it;
        }).map(contextSchemaService::update)
            .map(ContextSchemaConverter::toHolder).orElseThrow(() -> new ContextSchemaManagerException().setCode(ContextMangerErrors.CONTEXT_NOT_FOUND));
    }

    public ContextSchemaHolder resolveContextSchema(String workspaceId, JsonNode jsonNode) {
        Workspace workspace = workspaceService.findById(workspaceId).get();
        ContextSchema resolve = contextSchemaResolver.resolve(workspace, jsonNode);

        ContextSchemaHolder contextSchemaHolder = ContextSchemaConverter.toHolder(resolve);
        contextSchemaHolder.setNodeValues(getNodeValues(resolve, contextValuesExtractor.extractValues(jsonNode, null, null)));
        contextSchemaHolder.setRawPayload(jsonNode.toString());
        return contextSchemaHolder;
    }

    public ContextSchemaHolder resolveContextSchema(SchemaNode rootNode) {
        return ContextSchemaConverter.toHolder(contextSchemaResolver.resolve(rootNode));
    }

    public List<ContextSchemaValidationService.SchemaValidationEntry> validate(ContextSchemaHolder contextSchema) {
        return validationService.validate(new ContextSchema().setRootNode(contextSchema.getRootNode()));
    }

    private void validateContextSchemaAndThrowAnError(ContextSchema contextSchema) {
        List<ContextSchemaValidationService.SchemaValidationEntry> validationResult = validationService.validate(contextSchema);

        if (validationResult.stream().anyMatch(it -> it.getSeverity() == SeverityLevel.CRITICAL)) {
            throw new ContextSchemaValidationException().setSchemaValidationResult(validationResult);
        }
    }

    public void unlinkFromIntegrationPoint(String integrationPointKey) {
        contextSchemaService.updateAll(contextSchemaService.findByIntegrationPointKeys(Collections.singletonList(integrationPointKey), false).stream()
            .map(it -> it.setIntegrationPointKey(null)).collect(Collectors.toList()));
    }

    public List<ContextSchemaHolder> getAllByWorkspaceId(String workspaceId, boolean shortForm) {
        Map<String, IntegrationPoint> points = workspaceService.findById(workspaceId)
            .map(Workspace::getIntegrationPoints).stream().flatMap(Collection::stream).collect(Collectors.toMap(IntegrationPoint::getKey, it -> it));

        return contextSchemaService.findByIntegrationPointKeys(points.keySet(), shortForm).stream().map(ContextSchemaConverter::toHolder).collect(Collectors.toList());
    }

    public void deleteContextSchema(String contextSchemaId) {
        contextSchemaService.deleteById(contextSchemaId);
        segmentManager.unlinkFromContext(contextSchemaId);
    }

    public ContextSchemaHolder getById(String contextSchemaId) {
        return contextSchemaService.findById(contextSchemaId).map(ContextSchemaConverter::toHolder).orElse(null);
    }

    private Map<String, Object> getNodeValues(ContextSchema contextSchema, ContextValueHolder payload) {
        return contextSchema.getInlinePath().entrySet().stream()
            .filter(it -> it.getValue().getRootType() != SchemaNodeType.OBJECT && it.getValue().getSubType() != SchemaNodeType.OBJECT)
            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), payload.getValue(v.getKey())), HashMap::putAll);
    }

}
