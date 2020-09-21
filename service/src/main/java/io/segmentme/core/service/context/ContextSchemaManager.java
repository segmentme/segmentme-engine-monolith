package io.segmentme.core.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.converter.ContextSchemaConverter;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import io.segmentme.core.service.exception.ContextSchemaManagerException;
import io.segmentme.core.service.exception.ContextSchemaValidationException;
import io.segmentme.core.service.exception.error.ContextMangerErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.segmentme.core.service.exception.error.ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaManager {

    private final ContextSchemaValidationService validationService;

    private final ContextSchemaService contextSchemaService;

    private final ContextSchemaResolver contextSchemaResolver;

    private final WorkspaceService workspaceService;

    public ContextSchemaHolder create(String integrationPointKey, SchemaNode root) {
        if (workspaceService.findByIntegrationPointKey(integrationPointKey).isEmpty()) {
            throw new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND);
        }
        ContextSchema contextSchema = contextSchemaResolver.resolve(root);
        contextSchema.setIntegrationPointKey(integrationPointKey);

        validateContextSchema(contextSchema);
        return ContextSchemaConverter.toHolder(contextSchemaService.create(contextSchema));
    }

    public ContextSchemaHolder updateContextSchema(String id, SchemaNode root) {
        return contextSchemaService.findById(id).map(it -> {
            ContextSchema contextSchema = contextSchemaResolver.resolve(root);
            it.setInlinePath(contextSchema.getInlinePath());
            it.setRootNode(contextSchema.getRootNode());
            return it;
        }).map(contextSchemaService::update)
                .map(ContextSchemaConverter::toHolder).orElseThrow(() -> new ContextSchemaManagerException().setCode(ContextMangerErrors.CONTEXT_NOT_FOUND));
    }

    public ContextSchemaHolder resolveContextSchema(JsonNode jsonNode) {
        ContextSchema schema = contextSchemaResolver.resolve(jsonNode);
        validateContextSchema(schema);
        return ContextSchemaConverter.toHolder(schema);
    }

    private void validateContextSchema(ContextSchema contextSchema) {
        List<ContextSchemaValidationService.SchemaValidationEntry> validationResult = validationService.validate(contextSchema);

        if (validationResult.stream().anyMatch(it -> it.getSeverity() == SeverityLevel.CRITICAL)) {
            throw new ContextSchemaValidationException().setSchemaValidationResult(validationResult);
        }
    }
}
