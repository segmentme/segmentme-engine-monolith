package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.service.dto.WorkspaceHolder;

public class WorkspaceHolderConverter {


    public static WorkspaceHolder toHolder(Workspace workspace) {
        return new WorkspaceHolder()
                .setIntegrationPoints(workspace.getIntegrationPoints())
                .setWorkspaceConfiguration(workspace.getConfiguration())
                .setName(workspace.getName())
                .setId(workspace.getId());
    }
}
