package io.segmentme.core.service.workspace;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.domain.workpsace.*;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.converter.WorkspaceHolderConverter;
import io.segmentme.core.service.dto.WorkspaceHolder;
import io.segmentme.core.service.rule.RuleManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceManager {
    public static final List<String> DEFAULT_DATE_PATTERNS = Arrays.asList(
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'",
        DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'",
        DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
        DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern());

    private static final String DEFAULT = "Default";

    private final WorkspaceService workspaceService;

    private final RuleManager ruleManager;

    private final ContextSchemaManager contextSchemaManager;

    public WorkspaceHolder createDefaultWorkspace(User user) {
        return this.createWorkspace(user.getId(), DEFAULT);
    }

    public WorkspaceHolder getWorkspace(String workspaceId) {
        return workspaceService.findById(workspaceId).map(WorkspaceHolderConverter::toHolder).orElse(null);
    }

    public WorkspaceHolder createWorkspace(String ownerId, String name) {
        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setIntegrationPoints(Arrays.asList(generateIntegrationPoint().setName(DEFAULT)));
        workspace.setConfiguration(generateDefaultWorkspaceConfiguration());
        workspace.setUserProfiles(Arrays.asList(new UserProfile().setWorkspaceName(name).setRole(Role.OWNER).setUserId(ownerId)));
        return WorkspaceHolderConverter.toHolder(workspaceService.create(workspace));
    }

    public IntegrationPoint addIntegrationPoint(String workspaceId, String name) {
        IntegrationPoint integrationPoint = generateIntegrationPoint();
        integrationPoint.setName(name);
        workspaceService.findById(workspaceId).map(workspace -> {
            workspace.getIntegrationPoints().add(integrationPoint);
            return workspace;
        }).map(workspaceService::update);
        return integrationPoint;
    }


    public IntegrationPoint updateIntegrationPoint(String workspaceId, IntegrationPoint integrationPoint) {
        return workspaceService.findById(workspaceId).map(workspace -> {
            workspace.getIntegrationPoints()
                .stream()
                .filter(it -> it.getKey().equalsIgnoreCase(integrationPoint.getKey())).findAny()
                .map(it -> it.setName(integrationPoint.getName()));
            return workspaceService.update(workspace);
        }).map(it -> integrationPoint).orElse(null);

    }

    public void removeIntegrationPoint(String workspaceId, String integrationPointKey) {
        workspaceService.findById(workspaceId).map(workspace -> {
            workspace.getIntegrationPoints().removeIf(it -> it.getKey().equalsIgnoreCase(integrationPointKey));
            return workspace;
        }).ifPresent(workspaceService::update);

        ruleManager.unlinkFromIntegrationPoint(integrationPointKey);
        contextSchemaManager.unlinkFromIntegrationPoint(integrationPointKey);
    }


    WorkspaceConfiguration generateDefaultWorkspaceConfiguration() {
        return new WorkspaceConfiguration().setKnownDateFormats(DEFAULT_DATE_PATTERNS);
    }

    IntegrationPoint generateIntegrationPoint() {
        return new IntegrationPoint().setKey(UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY));
    }

    public void updateConfiguration(String id, WorkspaceConfiguration workspaceConfiguration) {
        workspaceService.findById(id).map(it -> {
            return it.setConfiguration(workspaceConfiguration);
        }).ifPresent(workspaceService::update);
    }
}
