package io.segmentme.core.service.user;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.service.user.UserService;
import io.segmentme.core.service.exception.UserManagerException;
import io.segmentme.core.service.exception.error.UserManagerErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManager {
    private final UserService userService;

    public void createUser(User user) throws UserManagerException {
        log.info("Create user {}", user);

        if(user.getId()!=null){
            throw new UserManagerException().setCode(UserManagerErrors.USER_SHOULD_NOT_HAVE_ID_ATTRIBUTE);
        }
    }
}
