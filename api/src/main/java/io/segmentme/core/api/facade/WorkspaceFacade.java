package io.segmentme.core.api.facade;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.WorkspaceDetails;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.core.service.dto.WorkspaceHolder;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WorkspaceFacade {

    private final WorkspaceManager workspaceManager;

    public WorkspaceDetails getWorkspaceDetails(String ownerId, String workspaceId) {
        return convertToWorkspaceDetails(workspaceManager.getWorkspace(workspaceId));
    }

    public WorkspaceDetails createWorkspace(String ownderId, String name) {
        return convertToWorkspaceDetails(workspaceManager.createWorkspace(ownderId, name));
    }

    private WorkspaceDetails convertToWorkspaceDetails(WorkspaceHolder holder) {
        return new WorkspaceDetails()
            .setId(holder.getId())
            .setName(holder.getName())
            .setIntegrationPoints(holder.getIntegrationPoints())
            .setConfiguration(holder.getWorkspaceConfiguration());
    }

    public IntegrationPoint addIntegrationPoint(AuthUser currentUser, String workspaceId, String name) {
        return workspaceManager.addIntegrationPoint(workspaceId, name);

    }

    public void updateConfiguration(String userId, String workspaceId, WorkspaceConfiguration workspaceConfiguration) {
        workspaceManager.updateConfiguration(workspaceId, workspaceConfiguration);
    }

    public void updateIntegrationPoint(String userId, String workspaceId, IntegrationPoint integrationPoint) {
        workspaceManager.updateIntegrationPoint(workspaceId, integrationPoint);
    }

    public void removeIntegrationPoint(String userId, String workspaceId, String key) {
        workspaceManager.removeIntegrationPoint(workspaceId, key);
    }
}
