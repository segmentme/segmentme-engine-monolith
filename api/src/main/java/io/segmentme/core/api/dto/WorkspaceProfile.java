package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.workpsace.Role;
import lombok.Data;

@Data
public class WorkspaceProfile {
    private String workspaceId;

    private String workspaceName;

    private Role role;

    private boolean active;

    private boolean isDefault;
}
