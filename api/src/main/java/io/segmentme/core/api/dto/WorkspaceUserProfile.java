package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.workpsace.Role;
import lombok.Data;

@Data
public class WorkspaceUserProfile {
    private Role role;

    private String name;

    private String email;

    private String profileId;

    private String userId;

}
