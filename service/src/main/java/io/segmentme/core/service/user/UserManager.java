package io.segmentme.core.service.user;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.service.user.UserService;
import io.segmentme.core.service.converter.UserHolderConverter;
import io.segmentme.core.service.dto.UserHolder;
import io.segmentme.core.service.dto.WorkspaceHolder;
import io.segmentme.core.service.exception.UserManagerException;
import io.segmentme.core.service.exception.error.UserManagerErrors;
import io.segmentme.core.service.workspace.UserProfileManager;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManager {
    private final UserService userService;

    private final WorkspaceManager workspaceManager;

    private final UserProfileManager userProfileManager;


    public UserHolder getById(String userId) {
        return UserHolderConverter.toHolder(userService.findById(userId).orElse(null));
    }

    public String switchWorkspace(String userId, String workspaceId) {
        User user = userService.findById(userId).orElse(null);
        if (userProfileManager.getUserProfiles(userId).stream().noneMatch(it -> it.getWorkspaceId().equalsIgnoreCase(workspaceId))) {
            throw new UserManagerException().setCode(UserManagerErrors.UNABLE_TO_SWITCH_WORKSPACE_DOESNT_EXISTS);
        }
        user.setLastActiveWorkspace(workspaceId);
        userService.update(user);
        return workspaceId;
    }

    public UserHolder createUser(UserHolder userToCreate)  {
        log.info("Create userToCreate {}", userToCreate);

        if (userService.findByEmail(userToCreate.getEmail()).isPresent()) {
            throw new UserManagerException().setCode(UserManagerErrors.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS);
        }

        User user = userService.create(UserHolderConverter.toUser(userToCreate));

        WorkspaceHolder defaultWorkspace = workspaceManager.createDefaultWorkspace(user);
        user.setLastActiveWorkspace(defaultWorkspace.getId());

        userService.update(user);

        return UserHolderConverter.toHolder(user);
    }


    public void acknowledgeUser(UserHolder holder) {
        if (userService.findByExternalId(holder.getId()).isPresent() || userService.findByEmail(holder.getEmail()).isPresent()) {
            return;
        }
        createUser(holder);
    }

    public List<User> getByIds(List<String> userIds) {
        return StreamSupport.stream(userService.findByIds(userIds).spliterator(), false)
            .collect(Collectors.toList());

    }

    public UserHolder getByExternalId(String externalId) {
        return UserHolderConverter.toHolder(userService.findByExternalId(externalId).get());
    }
}
