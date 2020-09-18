package io.segmentme.core.service.helper;

import io.segmentme.core.service.dto.UserHolder;
import io.segmentme.core.service.user.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserHolderHelper {

    private final UserManager userManager;

    public static UserHolder createUser() {
        return new UserHolder().setEmail("ababa@aa.com").setName("name").setPassword("PWD");
    }

    public UserHolder createUserAndState() {
        return userManager.createUser(createUser());
    }
}
