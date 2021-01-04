package io.segmentme.core.api.security;

import io.segmentme.core.api.config.SecurityUtils;
import io.segmentme.core.db.domain.context.DbObject;
import io.segmentme.core.db.domain.workpsace.UserProfile;
import io.segmentme.core.db.service.user.UserService;
import io.segmentme.core.service.workspace.UserProfileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSecurityService {

    private final UserProfileManager userProfileManager;
    private final UserService userService;

    public boolean isWorkspaceMember(String id) {
        return userProfileManager.getUserProfiles(userService.findByExternalId(SecurityUtils.currentUserId()).map(DbObject::getId).orElse(null))
            .stream()
            .map(UserProfile::getWorkspaceId)
            .map(it -> it.equalsIgnoreCase(id))
            .findFirst()
            .isPresent();
    }
}
