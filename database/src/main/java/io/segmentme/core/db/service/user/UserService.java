package io.segmentme.core.db.service.user;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.repository.UserRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Service
public class UserService extends AbstractDatabaseService<User, UserRepository> {

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findByExternalId(String externalId) {
        return repository.findByExternalId(externalId);
    }
}
