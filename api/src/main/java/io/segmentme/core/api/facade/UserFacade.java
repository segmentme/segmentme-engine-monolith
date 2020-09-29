package io.segmentme.core.api.facade;

import io.segmentme.core.api.dto.UserBasicInfo;
import io.segmentme.core.api.dto.UserDetails;
import io.segmentme.core.api.dto.WorkspaceProfile;
import io.segmentme.core.db.domain.workpsace.UserProfile;
import io.segmentme.core.service.dto.UserHolder;
import io.segmentme.core.service.user.UserManager;
import io.segmentme.core.service.workspace.UserProfileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserManager userManager;

    private final UserProfileManager userProfileManager;

    public UserDetails getUserDetails(String userId) {
        return getUserDetails(userManager.getById(userId));
    }

    private UserDetails getUserDetails(UserHolder createdUser) {
        List<UserProfile> userProfiles = userProfileManager.getUserProfiles(createdUser.getId());
        return new UserDetails()
                .setUserBasicInfo(convertToBasicUserInfo(createdUser))
                .setProfiles(userProfiles.stream()
                        .map(this::convertToWorkspaceProfile)
                        .map(it -> it.setActive(it.getWorkspaceId().equals(createdUser.getLastActiveWorkspace()))).collect(Collectors.toList()));
    }

    private UserBasicInfo convertToBasicUserInfo(UserHolder createdUser) {
        return new UserBasicInfo().setEmail(createdUser.getEmail()).setId(createdUser.getId()).setName(createdUser.getName());
    }

    private WorkspaceProfile convertToWorkspaceProfile(UserProfile userProfile) {
        return new WorkspaceProfile().setRole(userProfile.getRole())
                .setWorkspaceId(userProfile.getWorkspaceId())
                .setWorkspaceName(userProfile.getWorkspaceName());
    }

    public void switchWorkspace(String userId, String workspaceId) {
        userManager.switchWorkspace(userId, workspaceId);
    }

    public void acknowledgeUser(String id, String email) {
        userManager.acknowledgeUser(new UserHolder().setEmail(email).setId(id));
    }
}
