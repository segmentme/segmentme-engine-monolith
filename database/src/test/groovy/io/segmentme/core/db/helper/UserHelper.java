package io.segmentme.core.db.helper;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserHelper {

    private final UserRepository userRepository;

    public static User createUser() {
        return new User().setEmail("ababa@aa.com").setName("name").setPassword("PWD");
    }

    public User createAndSaveUser() {
        return userRepository.save(createUser());
    }
}
