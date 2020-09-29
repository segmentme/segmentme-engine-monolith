package io.segmentme.core.service.helper;

import io.segmentme.core.service.dto.UserHolder;
import io.segmentme.core.service.user.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserHolderHelper {

    private final UserManager userManager;

    public static UserHolder createUser() {
        return new UserHolder().setEmail(UUID.randomUUID().toString() + "@aa.com").setName("name");
    }

    public UserHolder createUserAndState() {
        return userManager.createUser(createUser());
    }
}
