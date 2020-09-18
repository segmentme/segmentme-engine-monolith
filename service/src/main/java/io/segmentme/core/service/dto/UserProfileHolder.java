package io.segmentme.core.service.dto;

import io.segmentme.core.db.domain.workpsace.Role;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserProfileHolder {
    private String userId;

    private String workspaceId;

    private Role role;
}
