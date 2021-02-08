package io.segmentme.core.api.facade;

import io.segmentme.core.api.dto.context.ContextSchemaShortInfo;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.dto.analysis.SdkAnalysisRequest;
import io.segmentme.core.service.dto.analysis.SdkAnalysisResponse;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
import io.segmentme.core.service.exception.ContextSchemaManagerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class SdkFacade {
    private final ContextSchemaManager contextSchemaManager;

    private final WorkspaceService workspaceService;

    private final AnalysisService analysisService;

    public SdkAnalysisResponse analyze(String integrationPointKey, SdkAnalysisRequest sdkAnalysisRequest) {
        log.info("Start facade analysis");
        SdkAnalysisResponse response = new SdkAnalysisResponse();
        if (StringUtils.isEmpty(sdkAnalysisRequest.getContextId())) {
            response.setContextId(this.actualizeSchema(integrationPointKey, sdkAnalysisRequest).getId());
        }
        SdkAnalysisResponse sdkAnalysisResponse = response.setAnalyzedSegments(this.analysisService.analyze(integrationPointKey, response.getContextId(), sdkAnalysisRequest.getAnalysisData()));
        log.info("End facade analysis");
        return sdkAnalysisResponse;
    }

    private ContextSchemaShortInfo actualizeSchema(String integrationPointKey, SdkAnalysisRequest payload) {
        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey).orElseThrow(() -> new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND));
        ContextSchemaHolder resolvedSchema = contextSchemaManager.resolveContextSchema(workspace, payload.getAnalysisData().getPayload());
        if (resolvedSchema.getInlinePath().entrySet().stream().anyMatch(it -> it.getValue().getRootType() == SchemaNodeType.UNDEFINED || it.getValue().getSubType() == SchemaNodeType.UNDEFINED)) {
            log.warn("Integration point key : {} Schema {} contains undefined values", integrationPointKey, payload.getAnalysisData().getPayload());
        }

        String hash = StringUtils.isNoneBlank(payload.getContextKey()) ? payload.getContextKey() : contextSchemaManager.computeHash(resolvedSchema);
        resolvedSchema.setHash(hash);
        ContextSchemaHolder existedSchema = contextSchemaManager.findByHash(integrationPointKey, hash);
        ContextSchemaHolder actualizedContext;
        String payloadAsString = payload.getAnalysisData().getPayloadAsString();

        if (existedSchema == null) {
            actualizedContext = contextSchemaManager.create(integrationPointKey, resolvedSchema.getRootNode(), payload.getContextKey(), payloadAsString, hash);
        } else {
            resolvedSchema.setName(existedSchema.getName());
            resolvedSchema.setIntegrationPointKey(integrationPointKey);
            resolvedSchema.setRawPayload(payloadAsString);
            resolveUnknownProperties(resolvedSchema, existedSchema);
            actualizedContext = contextSchemaManager.updateContextSchema(existedSchema.getId(), resolvedSchema);
        }


        return new ContextSchemaShortInfo().setId(actualizedContext.getId()).setIntegrationPointKey(integrationPointKey).setHash(actualizedContext.getHash());
    }

    private void resolveUnknownProperties(ContextSchemaHolder resolvedSchema, ContextSchemaHolder existedSchema) {
        Map<String, ContextSchema.InlineType> resolvedUndefinedPaths = resolvedSchema.getInlinePath().entrySet().stream().filter(it -> it.getValue().getRootType() == SchemaNodeType.UNDEFINED || it.getValue().getSubType() == SchemaNodeType.UNDEFINED)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (existedSchema.getInlinePath() == null) {
            existedSchema.setInlinePath(new HashMap<>());
        }
        resolvedUndefinedPaths.entrySet().stream().filter(it -> wasResolvedBefore(it.getKey(), existedSchema.getInlinePath())).forEach(it -> updateType(it.getKey(), resolvedSchema, existedSchema));
    }

    private void updateType(String key, ContextSchemaHolder resolvedSchema, ContextSchemaHolder existedSchema) {
        ContextSchema.InlineType actualType = existedSchema.getInlinePath().get(key);
        resolvedSchema.getInlinePath().put(key, actualType);

        SchemaNode nodeToUpdate = getNode(resolvedSchema.getRootNode(), key);
        if (nodeToUpdate == null) {
            return;
        }
        nodeToUpdate.setType(actualType.getRootType());
        nodeToUpdate.setSubType(actualType.getSubType());

    }

    private SchemaNode getNode(SchemaNode rootNode, String key) {
        if (StringUtils.isBlank(rootNode.getPath()) ||
            (!rootNode.getPath().equalsIgnoreCase(key)) && CollectionUtils.isNotEmpty(rootNode.getSubNodes())) {
            return rootNode.getSubNodes().stream().map(it -> getNode(it, key)).filter(Objects::nonNull).findFirst().orElse(null);
        }

        if (rootNode.getPath().equalsIgnoreCase(key)) {
            return rootNode;
        }

        return null;
    }

    private boolean wasResolvedBefore(String key, Map<String, ContextSchema.InlineType> inlinePath) {
        ContextSchema.InlineType inlineType = inlinePath.get(key);
        return inlineType!=null && (inlineType.getRootType() != SchemaNodeType.UNDEFINED || inlineType.getSubType() != SchemaNodeType.UNDEFINED);
    }


    public IntegrationPoint connect(String integrationPointKey) {
        return workspaceService.findByIntegrationPointKey(integrationPointKey)
            .map(Workspace::getIntegrationPoints).stream()
            .flatMap(Collection::stream)
            .filter(it -> it.getKey().equalsIgnoreCase(integrationPointKey))
            .findFirst().orElseThrow(() -> new RuntimeException("Not found"));

    }


}
