package io.segmentme.core.service.user;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.service.user.UserService;
import io.segmentme.core.service.converter.UserHolderConverter;
import io.segmentme.core.service.dto.UserHolder;
import io.segmentme.core.service.exception.UserManagerException;
import io.segmentme.core.service.exception.error.UserManagerErrors;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManager {
    private final UserService userService;

    private final WorkspaceManager workspaceManager;

    public UserHolder createUser(UserHolder userToCreate) throws UserManagerException {
        log.info("Create userToCreate {}", userToCreate);

        if (userToCreate.getId() != null) {
            throw new UserManagerException().setCode(UserManagerErrors.USER_SHOULD_NOT_HAVE_ID_ATTRIBUTE);
        }

        if (userService.findByEmail(userToCreate.getEmail()).isPresent()) {
            throw new UserManagerException().setCode(UserManagerErrors.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS);
        }

        User user = userService.create(UserHolderConverter.toUser(userToCreate));

        workspaceManager.createDefaultWorkspace(user);

        return UserHolderConverter.toHolder(user);
    }
}
