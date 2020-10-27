package io.segmentme.core.api.security;

import io.segmentme.core.db.domain.workpsace.UserProfile;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.workspace.UserProfileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceSecurityService {

    private final UserProfileManager userProfileManager;

    public boolean isMyWorkspace(String id, String userId){
        return userProfileManager.getUserProfiles(userId)
                .stream()
                .map(UserProfile::getWorkspaceId)
                .map(it -> it.equalsIgnoreCase(id))
                .findFirst()
                .isPresent();
    }
}
