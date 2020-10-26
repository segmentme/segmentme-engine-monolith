package io.segmentme.core.api.facade;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.context.*;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.ContextValidationErrors.NODE_SUBTYPE_NOT_DEFINED;
import static io.segmentme.core.service.exception.error.ContextValidationErrors.NODE_TYPE_NOT_DEFINED;

@Service
@RequiredArgsConstructor
public class ContextSchemaFacade {
    private final ContextSchemaManager contextSchemaManager;

    public List<ContextSchemaBasicInfo> getByWorkspace(String userId, String workspaceId, boolean shortForm) {
        return contextSchemaManager.getAllByWorkspaceId(workspaceId, shortForm).stream()
            .map(this::convertToBasicDto).collect(Collectors.toList());
    }

    public ContextSchemaResolveResult resolve(String userId, String workspaceId, JsonNode payload) {
        ContextSchemaHolder contextSchema = contextSchemaManager.resolveContextSchema(workspaceId, payload);
        return new ContextSchemaResolveResult()
            .setContextSchema(this.convertToFullDetailsDto(contextSchema))
            .setValidationEntries(contextSchemaManager.validate(contextSchema));
    }


    public ContextSchemaValidationResult validate(String userId, String workspaceId, ContextSchemaValidationRequest request) {
        ContextSchemaResolveResult originalSchema = this.resolve(userId, workspaceId, request.getRawPayload());
        ContextSchemaHolder updatedSchema = contextSchemaManager.resolveContextSchema(request.getRootNode());


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
        return new ContextSchemaValidationResult().setValidationEntries(originalSchema.getValidationEntries());
    }

    public ContextSchemaBasicInfo create(String id, String workspaceId, ContextSchemaCreateRequest request) {
        ContextSchemaHolder contextSchemaHolder = contextSchemaManager.create(request.getIntegrationPointKey(), request.getRootNode(), request.getName(), request.getRawPayload());
        return this.convertToBasicDto(contextSchemaHolder);
    }

    public void delete(AuthUser authUser, String contextSchemaId) {
        contextSchemaManager.deleteContextSchema(contextSchemaId);
    }

    public ContextSchemaFullDetails getById(AuthUser authUser, String contextSchemaId) {
        return convertToFullDetailsDto(contextSchemaManager.getById(contextSchemaId));
    }

    private ContextSchemaFullDetails convertToFullDetailsDto(ContextSchemaHolder holder) {
        return (ContextSchemaFullDetails) new ContextSchemaFullDetails()
            .setRootNode(holder.getRootNode())
            .setRawPayload(holder.getRawPayload())
            .setNodeValues(holder.getNodeValues()).setInlinePath(holder.getInlinePath())
            .setId(holder.getId())
            .setIntegrationPointKey(holder.getIntegrationPointKey())
            .setHash(holder.getHash())
            .setName(holder.getName());
    }

    public ContextSchemaBasicInfo update(String id, String contextId, ContextSchemaUpdateRequest payload) {
        return convertToBasicDto(contextSchemaManager.updateContextSchema(contextId, new ContextSchemaHolder()
            .setName(payload.getName())
            .setIntegrationPointKey(payload.getIntegrationPointKey())
            .setRootNode(payload.getRootNode())));
    }


    private ContextSchemaBasicInfo convertToBasicDto(ContextSchemaHolder contextSchema) {
        return (ContextSchemaBasicInfo) new ContextSchemaBasicInfo()
            .setInlinePath(contextSchema.getInlinePath())
            .setHash(contextSchema.getHash())
            .setIntegrationPointKey(contextSchema.getIntegrationPointKey())
            .setName(contextSchema.getName())
            .setId(contextSchema.getId());
    }

}
