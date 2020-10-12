package io.segmentme.core.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.condition.ConditionManager;
import io.segmentme.core.service.converter.ContextSchemaConverter;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import io.segmentme.core.service.exception.ContextSchemaManagerException;
import io.segmentme.core.service.exception.ContextSchemaValidationException;
import io.segmentme.core.service.exception.error.ContextMangerErrors;
import io.segmentme.core.service.rule.RuleManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    private final RuleManager ruleManager;

    private final ConditionManager conditionManager;

    public ContextSchemaHolder create(String integrationPointKey, SchemaNode root, String name, String rawPayload) {
        if (workspaceService.findByIntegrationPointKey(integrationPointKey).isEmpty()) {
            throw new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND);
        }
        ContextSchema contextSchema = contextSchemaResolver.resolve(root);
        contextSchema.setIntegrationPointKey(integrationPointKey);
        contextSchema.setName(name);
        contextSchema.setRawPayload(rawPayload);

        validateContextSchemaAndThrowAnError(contextSchema);
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

    public ContextSchemaHolder resolveContextSchema(String workspaceId, JsonNode jsonNode) {
        return ContextSchemaConverter.toHolder(contextSchemaResolver.resolve(workspaceService.findById(workspaceId).get(), jsonNode));
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
        contextSchemaService.findByIntegrationPointKey(integrationPointKey)
            .map(it -> it.setIntegrationPointKey(null)).ifPresent(contextSchemaService::update);
    }

    public List<ContextSchemaHolder> getAllByWorkspaceId(String workspaceId, boolean shortForm) {
        Map<String, IntegrationPoint> points = workspaceService.findById(workspaceId)
            .map(Workspace::getIntegrationPoints).stream().flatMap(Collection::stream).collect(Collectors.toMap(IntegrationPoint::getKey, it -> it));

        return contextSchemaService.findByIntegrationPointKeys(points.keySet(), shortForm).stream().map(ContextSchemaConverter::toHolder).collect(Collectors.toList());
    }

    public void deleteContextSchema(String contextSchemaId) {

        contextSchemaService.deleteById(contextSchemaId);
        ruleManager.unlinkFromContext(contextSchemaId);
        conditionManager.unlinkFromContextId(contextSchemaId);

    }

    public ContextSchemaHolder getById(String contextSchemaId) {
        return contextSchemaService.findById(contextSchemaId).map(ContextSchemaConverter::toHolder).orElse(null);
    }
}
