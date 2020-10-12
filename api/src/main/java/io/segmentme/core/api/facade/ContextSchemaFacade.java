package io.segmentme.core.api.facade;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.ContextSchemaCreateRequest;
import io.segmentme.core.api.dto.ContextSchemaDetails;
import io.segmentme.core.api.dto.ContextSchemaValidationResult;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.analysis.CriteriaValueLocator;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContextSchemaFacade {
    private final ContextSchemaManager contextSchemaManager;

    private final ContextValuesExtractor contextValuesExtractor;

    public List<ContextSchemaDetails> getByWorkspace(String userId, String workspaceId) {
        return contextSchemaManager.getAllByWorkspaceId(workspaceId, true).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ContextSchemaDetails convertToDto(ContextSchemaHolder contextSchema) {
        return new ContextSchemaDetails().setInlinePath(contextSchema.getInlinePath())
            .setIntegrationPointKey(contextSchema.getIntegrationPointKey())
            .setName(contextSchema.getName())
            .setId(contextSchema.getId())
            .setRootNode(contextSchema.getRootNode());
    }

    public ContextSchemaValidationResult resolve(String userId, String workspaceId, JsonNode payload) {
        ContextSchemaHolder contextSchema = contextSchemaManager.resolveContextSchema(workspaceId, payload);
        return new ContextSchemaValidationResult().setContextSchema(this.convertToDto(contextSchema))
            .setValidationEntries(contextSchemaManager.validate(contextSchema))
            .setNodeValues(getNodeValues(contextSchema, contextValuesExtractor.extractValues(payload, null, null)));
    }

    private Map<String, Object> getNodeValues(ContextSchemaHolder contextSchema, ContextValueHolder payload) {
        return contextSchema.getInlinePath().entrySet().stream()
            .filter(it -> it.getValue().getRootType() != SchemaNodeType.OBJECT && it.getValue().getSubType() != SchemaNodeType.OBJECT)
            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), CriteriaValueLocator.getCriteriaValue(v.getKey(), payload)), HashMap::putAll);


    }

    public ContextSchemaValidationResult validate(String userId, String workspaceId, ContextSchemaDetails contextSchema) {
        return new ContextSchemaValidationResult()
            .setContextSchema(contextSchema)
            .setValidationEntries(contextSchemaManager.validate(new ContextSchemaHolder().setRootNode(contextSchema.getRootNode())));
    }

    public ContextSchemaDetails create(String id, String workspaceId, ContextSchemaCreateRequest request) {
        ContextSchemaHolder contextSchemaHolder = contextSchemaManager.create(request.getIntegrationPointKey(), request.getRootNode(), request.getName(), request.getRawPayload());
        return this.convertToDto(contextSchemaHolder);
    }

    public void delete(AuthUser authUser, String contextSchemaId) {
        contextSchemaManager.deleteContextSchema(contextSchemaId);
    }

    public ContextSchemaDetails getById(AuthUser authUser, String contextSchemaId) {
        return convertToDto(contextSchemaManager.getById(contextSchemaId));
    }
}
