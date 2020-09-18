package io.segmentme.core.service.workspace;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.domain.workpsace.*;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.converter.WorkspaceHolderConverter;
import io.segmentme.core.service.dto.WorkspaceHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceManager {
    private static final List<String> DEFAULT_DATE_PATTERNS = Arrays.asList(
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'",
            DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
            DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'",
            DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
            DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern());

    private static final String DEFAULT = "DEFAULT";

    private final WorkspaceService workspaceService;

    public WorkspaceHolder createDefaultWorkspace(User user) {
        return this.createWorkspace(user.getId(), DEFAULT);
    }

    public WorkspaceHolder createWorkspace(String ownerId, String name) {
        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setIntegrationPoints(Arrays.asList(generateIntegrationPoint()));
        workspace.setConfiguration(generateDefaultWorkspaceConfiguration());
        workspace.setUserProfiles(Arrays.asList(new UserProfile().setRole(Role.OWNER).setUserId(ownerId)));
        return WorkspaceHolderConverter.toHolder(workspaceService.create(workspace));
    }

    private WorkspaceConfiguration generateDefaultWorkspaceConfiguration() {
        return new WorkspaceConfiguration().setKnownDateFormats(DEFAULT_DATE_PATTERNS);
    }

    private IntegrationPoint generateIntegrationPoint() {
        return new IntegrationPoint().setKey(UUID.randomUUID().toString());
    }
}
