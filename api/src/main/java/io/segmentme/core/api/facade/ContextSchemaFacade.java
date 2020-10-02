package io.segmentme.core.api.facade;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.dto.ContextSchemaDetails;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContextSchemaFacade {
    private final ContextSchemaManager contextSchemaManager;

    public List<ContextSchemaDetails> getByWorkspace(String userId, String workspaceId) {
        return contextSchemaManager.getAllByWorkspaceId(workspaceId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ContextSchemaDetails convertToDto(ContextSchemaHolder contextSchema) {
        return new ContextSchemaDetails().setInlinePath(contextSchema.getInlinePath())
            .setIntegrationPointKey(contextSchema.getIntegrationPointKey())
            .setIntegrationPointName(contextSchema.getIntegrationPointName())
            .setRootNode(contextSchema.getRootNode());
    }

    public ContextSchemaHolder resolve(String userId, String workspaceId, JsonNode payload) {
        return contextSchemaManager.resolveContextSchema(workspaceId, payload);
    }
}
