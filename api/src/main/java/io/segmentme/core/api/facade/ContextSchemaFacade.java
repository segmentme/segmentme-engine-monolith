package io.segmentme.core.api.facade;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.ContextSchemaCreateRequest;
import io.segmentme.core.api.dto.ContextSchemaDetails;
import io.segmentme.core.api.dto.ContextSchemaValidationRequest;
import io.segmentme.core.api.dto.ContextSchemaValidationResult;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.analysis.ContextValuesExtractor;
import io.segmentme.core.service.analysis.CriteriaValueLocator;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.context.ContextSchemaValidationService;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.ContextValidationErrors.NODE_SUBTYPE_NOT_DEFINED;
import static io.segmentme.core.service.exception.error.ContextValidationErrors.NODE_TYPE_NOT_DEFINED;

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

    public ContextSchemaValidationResult validate(String userId, String workspaceId, ContextSchemaValidationRequest contextSchema) {
        ContextSchemaValidationResult originalSchema = this.resolve(userId, workspaceId, contextSchema.getRawPayload());
        ContextSchemaHolder updatedSchema = contextSchemaManager.resolveContextSchema(contextSchema.getRootNode());

        List<ContextSchemaValidationService.SchemaValidationEntry> validate = contextSchemaManager.validate(updatedSchema);
        originalSchema.getContextSchema().setRootNode(updatedSchema.getRootNode());
        if (CollectionUtils.isNotEmpty(originalSchema.getValidationEntries())) {
            originalSchema.getContextSchema()
                .setInlinePath(originalSchema.getContextSchema().getInlinePath()
                    .entrySet().stream()
                    .filter(it -> updatedSchema.getInlinePath().containsKey(it.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            originalSchema.getValidationEntries().removeIf(it -> {
                ContextSchema.InlineType inlineType = updatedSchema.getInlinePath().get(it.getPath());
                if (inlineType == null) {
                    return true;
                } else if (it.getCode().equalsIgnoreCase(NODE_SUBTYPE_NOT_DEFINED)) {
                    return inlineType.getSubType() != null;
                } else if (it.getCode().equalsIgnoreCase(NODE_TYPE_NOT_DEFINED)) {
                    return inlineType.getRootType() != null;
                }
                return false;
            });
        }
        return originalSchema;
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
