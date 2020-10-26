package io.segmentme.core.api.facade;

import io.segmentme.core.api.dto.context.ContextSchemaShortInfo;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import io.segmentme.core.service.exception.ContextSchemaManagerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

import static io.segmentme.core.service.exception.error.ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class SdkFacade {
    private final ContextSchemaManager contextSchemaManager;

    private final WorkspaceService workspaceService;

    public ContextSchemaShortInfo actualizeSchema(String integrationPointKey, SchemaNode rootNode) {
        if (workspaceService.findByIntegrationPointKey(integrationPointKey).isEmpty()) {
            throw new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND);
        }

        ContextSchemaHolder resolvedSchema = contextSchemaManager.resolveContextSchema(rootNode);

        if (resolvedSchema.getInlinePath().entrySet().stream().anyMatch(it -> it.getValue().getRootType() == SchemaNodeType.UNDEFINED || it.getValue().getSubType() == SchemaNodeType.UNDEFINED)) {
            log.warn("Integration point key : {} Schema {} contains undefined values", integrationPointKey, rootNode);
        }

        ContextSchemaHolder byHash = contextSchemaManager.findByHash(integrationPointKey, resolvedSchema.getHash());
        ContextSchemaHolder actualizedContext;
        if (byHash == null) {
            actualizedContext = contextSchemaManager.create(integrationPointKey, rootNode, "sdk-schema_" + LocalDateTime.now(), null);
        } else {
            resolvedSchema.setName(byHash.getName());
            resolvedSchema.setIntegrationPointKey(integrationPointKey);
            actualizedContext = contextSchemaManager.updateContextSchema(byHash.getId(), resolvedSchema);
        }

        return new ContextSchemaShortInfo().setId(resolvedSchema.getId()).setIntegrationPointKey(integrationPointKey).setHash(actualizedContext.getHash());
    }

    public IntegrationPoint connect(String integrationPointKey) {
        return workspaceService.findByIntegrationPointKey(integrationPointKey)
            .map(Workspace::getIntegrationPoints).stream()
            .flatMap(Collection::stream)
            .filter(it -> it.getKey().equalsIgnoreCase(integrationPointKey))
            .findFirst().orElseThrow(() -> new RuntimeException("Not found"));

    }
}
