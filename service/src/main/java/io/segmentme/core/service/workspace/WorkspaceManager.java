package io.segmentme.core.service.workspace;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.domain.workpsace.*;
import io.segmentme.core.db.service.workspace.UserProfileService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.context.ContextSchemaManager;
import io.segmentme.core.service.converter.WorkspaceHolderConverter;
import io.segmentme.core.service.dto.WorkspaceHolder;
import io.segmentme.core.service.exception.WorkspaceManagerException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

import static io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration.DEFAULT_DATE_PATTERNS;
import static io.segmentme.core.service.exception.error.WorkspaceManagerErrors.UNABLE_TO_DELETE_DEFAULT_WORKSAPCE;

@Service
@RequiredArgsConstructor
public class WorkspaceManager {
    private static final String DEFAULT = "Default";
    public static final Comparator<String> DATE_COMPARATOR = (o1, o2) -> {

        if (DEFAULT_DATE_PATTERNS.contains(o1) && DEFAULT_DATE_PATTERNS.contains(o2)) {
            return DEFAULT_DATE_PATTERNS.indexOf(o1) - DEFAULT_DATE_PATTERNS.indexOf(o2);
        } else if (DEFAULT_DATE_PATTERNS.contains(o1)) {
            return 1;
        } else if (DEFAULT_DATE_PATTERNS.contains(o2)) {
            return -1;
        }

        return 1;
    };

    private final WorkspaceService workspaceService;

    private final SegmentManager segmentManager;

    private final ContextSchemaManager contextSchemaManager;

    private final UserProfileService userProfileService;

    public WorkspaceHolder createDefaultWorkspace(User user) {
        return this.createWorkspace(user.getId(), DEFAULT, true);
    }

    public WorkspaceHolder getWorkspace(String workspaceId) {
        return workspaceService.findById(workspaceId).map(WorkspaceHolderConverter::toHolder).orElse(null);
    }

    private WorkspaceHolder createWorkspace(String ownerId, String name, boolean isDefault) {
        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setIntegrationPoints(Arrays.asList(generateIntegrationPoint().setName(DEFAULT)));
        workspace.setConfiguration(generateDefaultWorkspaceConfiguration());
        workspace.setUserProfiles(Arrays.asList(new UserProfile().setWorkspaceName(name).setDefault(isDefault).setRole(Role.OWNER).setUserId(ownerId)));
        workspace.setDefault(isDefault);
        return WorkspaceHolderConverter.toHolder(workspaceService.create(workspace));
    }

    public WorkspaceHolder createWorkspace(String ownerId, String name) {
        return createWorkspace(ownerId, name, false);
    }

    public IntegrationPoint addIntegrationPoint(String workspaceId, String name) {
        IntegrationPoint integrationPoint = generateIntegrationPoint();
        integrationPoint.setName(name);
        workspaceService.findById(workspaceId).map(workspace -> {
            workspace.getIntegrationPoints().add(integrationPoint);
            return workspace;
        }).ifPresent(workspaceService::update);
        return integrationPoint;
    }


    public IntegrationPoint updateIntegrationPoint(String workspaceId, IntegrationPoint integrationPoint) {
        return workspaceService.findById(workspaceId).map(workspace -> {
            workspace.getIntegrationPoints()
                .stream()
                .filter(it -> it.getKey().equalsIgnoreCase(integrationPoint.getKey())).findAny()
                .ifPresent(it -> it.setName(integrationPoint.getName()));
            return workspaceService.update(workspace);
        }).map(it -> integrationPoint).orElse(null);

    }

    public void removeIntegrationPoint(String workspaceId, String integrationPointKey) {
        workspaceService.findById(workspaceId).map(workspace -> {
            workspace.getIntegrationPoints().removeIf(it -> it.getKey().equalsIgnoreCase(integrationPointKey));
            return workspace;
        }).ifPresent(workspaceService::update);

        segmentManager.unlinkFromIntegrationPoint(integrationPointKey);
        contextSchemaManager.unlinkFromIntegrationPoint(integrationPointKey);
    }


    WorkspaceConfiguration generateDefaultWorkspaceConfiguration() {
        return new WorkspaceConfiguration().setKnownDateFormats(DEFAULT_DATE_PATTERNS);
    }

    IntegrationPoint generateIntegrationPoint() {
        return new IntegrationPoint().setKey(UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY));
    }


    public void removeWorkspace(String id) {
        workspaceService.findById(id).ifPresent(it -> {
                if (it.isDefault()) {
                    throw new WorkspaceManagerException().setCode(UNABLE_TO_DELETE_DEFAULT_WORKSAPCE);
                }
                it.getIntegrationPoints().forEach(integrationPoint -> removeIntegrationPoint(id, integrationPoint.getKey()));
                userProfileService.deleteAll(it.getUserProfiles());
                workspaceService.deleteById(id);
            }
        );
    }

    public void updateConfiguration(String id, WorkspaceHolder holder) {
        holder.getWorkspaceConfiguration().getKnownDateFormats().sort(DATE_COMPARATOR);
        workspaceService.findById(id)
            .map(it -> {
                    it.setConfiguration(holder.getWorkspaceConfiguration());
                    it.setName(holder.getName());
                    it.getUserProfiles().forEach(profile -> profile.setWorkspaceName(holder.getName()));
                    return it;
                }
            )
            .ifPresent(workspaceService::update);
    }


}

